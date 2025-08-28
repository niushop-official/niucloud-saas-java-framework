package com.niu.core.service.admin.generator.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.entity.generator.GenerateColumn;
import com.niu.core.entity.generator.GenerateTable;
import com.niu.core.enums.generator.SqlColumnEnum;
import com.niu.core.mapper.generator.GenerateColumnMapper;
import com.niu.core.mapper.generator.GenerateTableMapper;
import com.niu.core.service.admin.generator.IGenerateColumnService;
import com.niu.core.service.admin.generator.IGenerateService;
import com.niu.core.service.admin.generator.param.GenerateCodeParam;
import com.niu.core.service.admin.generator.param.GenerateEditParam;
import com.niu.core.service.admin.generator.param.GenerateParam;
import com.niu.core.service.admin.generator.param.GenerateSearchParam;
import com.niu.core.service.admin.generator.vo.*;
import com.niu.core.service.core.app.tools.SQLScriptRunnerTools;
import com.niu.core.service.core.generator.CoreGenerateService;
import com.niu.core.service.core.generator.vo.CoreGenerateTemplateVo;
import com.niu.core.service.core.generator.vo.MapperInfoVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@Log4j2
@Service
public class GenerateServiceImpl implements IGenerateService {

    @Resource
    private GenerateTableMapper generateTableMapper;
    @Resource
    private GenerateColumnMapper generateColumnMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Resource
    private IGenerateColumnService generateColumnService;

    @Override
    public PageResult<GenerateListVo> getPage(PageParam pageParam, GenerateSearchParam searchParam) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<GenerateTable> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ngt");
        queryWrapper.select("ngt.id, ngt.table_name, ngt.table_content, ngt.module_name, ngt.class_name, ngt.create_time, ngt.edit_type, ngt.addon_name, ngt.order_type, ngt.parent_menu, ngt.relations, ngt.synchronous_number, na.title, na.`key`");
        queryWrapper.leftJoin("?_addon na ON na.`key` = ngt.addon_name".replace("?_", GlobalConfig.tablePrefix));
        if (ObjectUtil.isNotEmpty(searchParam.getTableName())) {
            queryWrapper.like("ngt.table_name", searchParam.getTableName());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getTableContent())) {
            queryWrapper.eq("ngt.table_content", searchParam.getTableContent());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getAddonName())) {
            if (searchParam.getAddonName().equals("2")) {
                queryWrapper.eq("ngt.addon_name", "");
            } else {
                queryWrapper.like("ngt.addon_name", searchParam.getAddonName());
            }
        }

        queryWrapper.orderByDesc("ngt.create_time");

        IPage<GenerateListVo> iPage = generateTableMapper.selectJoinPage(new Page<>(page, limit), GenerateListVo.class, queryWrapper);
        return PageResult.build(iPage);
    }

    @Override
    public GenerateDetailVo getInfo(Integer id) {

        GenerateTable generateTable = generateTableMapper.selectById(id);
        if (ObjectUtil.isEmpty(generateTable)) throw new AdminException("生成表不存在");
        GenerateDetailVo vo = new GenerateDetailVo();
        BeanUtils.copyProperties(generateTable, vo);

        if (vo.getOrderType() != 0) {
            GenerateColumn orderColumn = generateColumnMapper.selectOne(new QueryWrapper<GenerateColumn>().eq("table_id", id).eq("is_order", 1));
            if (ObjectUtil.isNotEmpty(orderColumn)) {
                vo.setOrderColumnName(orderColumn.getColumnName());
            } else {
                vo.setOrderColumnName("");
            }
        }
        GenerateColumn deleteColumn = generateColumnMapper.selectOne(new QueryWrapper<GenerateColumn>().eq("table_id", id).eq("is_delete", 1));
        if (ObjectUtil.isNotEmpty(deleteColumn)) {
            vo.setDeleteColumnName(deleteColumn.getColumnName());
            vo.setIsDelete(1);
        } else {
            vo.setDeleteColumnName("");
            vo.setIsDelete(0);
        }
        List<GenerateColumn> columnList = generateColumnMapper.selectList(new QueryWrapper<GenerateColumn>().eq("table_id", id));
        if (ObjectUtil.isNotEmpty(columnList)) {
            List<GenerateColumnVo> columnVoList = new ArrayList<>();
            for (GenerateColumn column : columnList) {
                GenerateColumnVo generateColumnVo = new GenerateColumnVo();
                BeanUtils.copyProperties(column, generateColumnVo);
                if (column.getViewType().equals("number")) {
                    if (!column.getValidateType().isEmpty()) {
                        if (column.getValidateType().startsWith("[")) {
                            JSONArray numValidate = JSONUtil.parseArray(column.getValidateType());
                            if (numValidate.get(0).toString().equals("between")) {
                                generateColumnVo.setViewMax(JSONUtil.parseArray(numValidate.get(1)).get(1).toString());
                                generateColumnVo.setViewMin(JSONUtil.parseArray(numValidate.get(1)).get(0).toString());
                            } else if (numValidate.get(0).toString().equals("max")) {
                                generateColumnVo.setViewMax(JSONUtil.parseArray(numValidate.get(1)).get(0).toString());
                            } else if (numValidate.get(0).toString().equals("min")) {
                                generateColumnVo.setViewMin(JSONUtil.parseArray(numValidate.get(1)).get(0).toString());
                            } else {
                                generateColumnVo.setViewMax("100");
                                generateColumnVo.setViewMin("0");
                            }
                        } else {
                            generateColumnVo.setViewMax("100");
                            generateColumnVo.setViewMin("0");
                        }

                    } else {
                        generateColumnVo.setViewMax("100");
                        generateColumnVo.setViewMin("0");
                    }
                } else {
                    generateColumnVo.setViewMax("");
                    generateColumnVo.setViewMin("");
                }
                if (!column.getValidateType().isEmpty()) {
                    if (column.getValidateType().startsWith("[")) {
                        JSONArray num1Validate = JSONUtil.parseArray(column.getValidateType());
                        if (num1Validate.get(0).toString().equals("between")) {
                            generateColumnVo.setMaxNumber(JSONUtil.parseArray(num1Validate.get(1)).get(1).toString());
                            generateColumnVo.setMinNumber(JSONUtil.parseArray(num1Validate.get(1)).get(0).toString());
                        } else if (num1Validate.get(0).toString().equals("max")) {
                            generateColumnVo.setMaxNumber(JSONUtil.parseArray(num1Validate.get(1)).get(0).toString());
                        } else if (num1Validate.get(0).toString().equals("min")) {
                            generateColumnVo.setMinNumber(JSONUtil.parseArray(num1Validate.get(1)).get(0).toString());
                        } else {
                            generateColumnVo.setMaxNumber("120");
                            generateColumnVo.setMinNumber("1");
                        }
                    } else {
                        generateColumnVo.setMaxNumber("120");
                        generateColumnVo.setMinNumber("1");
                    }

                } else {
                    generateColumnVo.setMaxNumber("120");
                    generateColumnVo.setMinNumber("1");
                }
                if (!column.getModel().isEmpty()) {
                    generateColumnVo.setSelectType(2);
                } else {
                    generateColumnVo.setSelectType(1);
                }
                columnVoList.add(generateColumnVo);
            }
            vo.setTableColumn(columnVoList);
        }
        return vo;
    }

    @Override
    @Transactional
    public Integer add(GenerateParam generateParam) {

        String sql = "SHOW TABLE STATUS WHERE 1=1 ";
        String tablePrefix = GlobalConfig.tablePrefix;
        if (!generateParam.getTableName().isEmpty()) {
            sql += " AND Name = '" + generateParam.getTableName() + "'";
        }
        List<Map<String, Object>> listData = jdbcTemplate.queryForList(sql);
        if (ObjectUtil.isEmpty(listData)) throw new AdminException("数据表不存在");
        Map<String, Object> table = listData.get(0);
        if (ObjectUtil.isEmpty(table)) throw new AdminException("数据表不存在");
        String tableName = table.get("Name").toString().substring(tablePrefix.length());

        //添加生成表数据
        GenerateTable generateTable = new GenerateTable();
        generateTable.setTableName(tableName);
        generateTable.setTableContent(table.get("Comment").toString());
        generateTable.setClassName(tableName);
        generateTable.setCreateTime(System.currentTimeMillis() / 1000);
        generateTable.setModuleName(tableName);
        generateTableMapper.insert(generateTable);

        //添加生成字段数据
        List<Map<String, Object>> columns = jdbcTemplate.queryForList("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE()) and TABLE_NAME='" + tablePrefix + tableName + "'");
        Integer id = generateTable.getId();
        List<GenerateColumn> list = new ArrayList<>();
        for (Map<String, Object> column : columns) {
            GenerateColumn generateColumn = new GenerateColumn();

            generateColumn.setIsRequired(0);
            String[] defaultColumn = {"id", "create_time", "update_time"};
            if (column.get("IS_NULLABLE").toString().equals("NO") && !column.get("COLUMN_KEY").equals("PRI") && Arrays.asList(defaultColumn).contains(column.get("COLUMN_NAME").toString())) {
                generateColumn.setIsRequired(1);
            }

            generateColumn.setTableId(id);
            generateColumn.setColumnName(column.get("COLUMN_NAME").toString());
            generateColumn.setColumnType(getDbFieldType(column.get("DATA_TYPE").toString()));
            if (generateColumn.getColumnType().equals("Integer") && generateColumn.getColumnName().contains("time")) {
                generateColumn.setColumnType("Long");
            }
            generateColumn.setColumnComment(column.get("COLUMN_COMMENT").toString());
            generateColumn.setIsPk(column.get("COLUMN_KEY").equals("PRI") ? 1 : 0);
            generateColumn.setIsInsert(Arrays.asList(defaultColumn).contains(column.get("COLUMN_NAME").toString()) ? 0 : 1);
            generateColumn.setIsUpdate(Arrays.asList(defaultColumn).contains(column.get("COLUMN_NAME").toString()) ? 0 : 1);
            generateColumn.setIsLists(Arrays.asList(defaultColumn).contains(column.get("COLUMN_NAME").toString()) ? 0 : 1);
            generateColumn.setIsDelete(0);
            generateColumn.setQueryType("=");
            generateColumn.setViewType("input");
            generateColumn.setDictType("");
            generateColumn.setAddon("");
            generateColumn.setModel("");
            generateColumn.setLabelKey("");
            generateColumn.setValueKey("");
            generateColumn.setCreateTime(System.currentTimeMillis() / 1000);
            generateColumn.setUpdateTime(System.currentTimeMillis() / 1000);
            list.add(generateColumn);

        }
        generateColumnService.insertAll(list);
        return id;
    }

    @Override
    @Transactional
    public void edit(Integer id, GenerateEditParam generateParam) {
        //添加生成表数据
        GenerateTable generateTable = new GenerateTable();
        generateTable.setId(id);
        generateTable.setTableName(generateParam.getTableName());
        generateTable.setTableContent(generateParam.getTableContent());
        generateTable.setModuleName(generateParam.getModuleName());
        generateTable.setClassName(generateParam.getClassName());
        generateTable.setEditType(generateParam.getEditType());
        generateTable.setAddonName(generateParam.getAddonName());
        generateTable.setOrderType(generateParam.getOrderType());
        generateTable.setParentMenu(generateParam.getParentMenu());
        generateTable.setRelations(generateParam.getRelations());
        generateTableMapper.updateById(generateTable);
        //更新表字段
        generateColumnMapper.delete(new QueryWrapper<GenerateColumn>().eq("table_id", id));
        JSONArray columns = JSONUtil.parseArray(generateParam.getTableColumn());
        List<GenerateColumn> list = new ArrayList<>();

        for (int i = 0; i < columns.size(); i++) {
            GenerateColumn generateColumn = new GenerateColumn();
            JSONObject column = columns.getJSONObject(i);
            generateColumn.setTableId(id);
            generateColumn.setColumnName(column.getStr("column_name"));
            generateColumn.setColumnComment(column.getStr("column_comment"));
            generateColumn.setIsPk(column.getInt("is_pk"));
            generateColumn.setIsRequired(column.getInt("is_required"));
            generateColumn.setIsInsert(column.getInt("is_insert"));
            generateColumn.setIsUpdate(column.getInt("is_update"));
            generateColumn.setIsLists(column.getInt("is_lists"));
            generateColumn.setIsSearch(column.getInt("is_search"));
            generateColumn.setIsDelete(0);
            generateColumn.setIsOrder(0);
            generateColumn.setQueryType(column.getStr("query_type"));
            generateColumn.setViewType(ObjectUtil.isEmpty(column.getStr("view_type")) ? "input" : column.getStr("view_type"));
            generateColumn.setDictType(ObjectUtil.isEmpty(column.getStr("dict_type")) ? "" : column.getStr("dict_type"));
            generateColumn.setAddon(ObjectUtil.isEmpty(column.getStr("addon")) ? "" : column.getStr("addon"));
            generateColumn.setModel(ObjectUtil.isEmpty(column.getStr("model")) ? "" : column.getStr("model"));
            generateColumn.setLabelKey(ObjectUtil.isEmpty(column.getStr("label_key")) ? "" : column.getStr("label_key"));
            generateColumn.setValueKey(ObjectUtil.isEmpty(column.getStr("value_key")) ? "" : column.getStr("value_key"));
            generateColumn.setUpdateTime(System.currentTimeMillis() / 1000);
            generateColumn.setCreateTime(System.currentTimeMillis() / 1000);
            generateColumn.setColumnType(ObjectUtil.isEmpty(column.getStr("column_type")) ? "String" : column.getStr("column_type"));
            generateColumn.setValidateType(ObjectUtil.isEmpty(column.getStr("validate_type")) ? "" : column.getStr("validate_type"));
            //传入字段rule暂时不知含义，待定

            if (generateParam.getIsDelete() == 1) {
                if (column.getStr("column_name").equals(generateParam.getDeleteColumnName())) {
                    generateColumn.setIsDelete(1);
                }
            }
            if (generateParam.getOrderType() != 0) {
                if (column.getStr("column_name").equals(generateParam.getOrderColumnName())) {
                    generateColumn.setIsOrder(1);
                }
            }

            if (ObjectUtil.isNotEmpty(column.getStr("validate_type")) && !column.getStr("view_type").equals("number")) {
                if (column.getStr("validate_type").equals("between")) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.add("between");
                    jsonArray.add(new String[]{column.getStr("min_number"), column.getStr("max_number")});
                    generateColumn.setValidateType(jsonArray.toString());
                } else if (column.getStr("validate_type").equals("max")) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.add("max");
                    jsonArray.add(new String[]{column.getStr("max_number")});
                    generateColumn.setValidateType(jsonArray.toString());
                } else if (column.getStr("validate_type").equals("min")) {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.add("min");
                    jsonArray.add(new String[]{column.getStr("min_number")});
                    generateColumn.setValidateType(jsonArray.toString());
                }
            }

            if (column.getStr("view_type").equals("number")) {
                JSONArray numJsonArray = new JSONArray();
                numJsonArray.add("between");
                numJsonArray.add(new String[]{column.getStr("view_min"), column.getStr("view_max")});
                generateColumn.setValidateType(numJsonArray.toString());
            }
            if (ObjectUtil.isNotEmpty(column.getStr("model"))) {
                generateColumn.setDictType("");
            }
            list.add(generateColumn);

        }

        generateColumnService.insertAll(list);

    }

    @Override
    @Transactional
    public void del(Integer id) {

        generateTableMapper.deleteById(id);
        generateColumnMapper.delete(new QueryWrapper<GenerateColumn>().eq("table_id", id));
    }

    @Override
    public void generate(GenerateCodeParam generateCodeParam) {
        GenerateTable generateTable = generateTableMapper.selectById(generateCodeParam.getId());
        List<GenerateColumn> columnList = generateColumnMapper.selectList(new QueryWrapper<GenerateColumn>().eq("table_id", generateCodeParam.getId()));
        CoreGenerateService coreGenerateService = new CoreGenerateService();
        List<CoreGenerateTemplateVo> list = coreGenerateService.generateCode(generateTable, columnList);

        // 下载
        if (generateCodeParam.getGenerateType().equals("2")) {
            String tempDir = WebAppEnvs.get().webRootDownResource + "upload/generate/";
            String packageDir = tempDir + "package/";
            FileTools.createDirs(packageDir);
            FileUtil.clean(tempDir);
            for (CoreGenerateTemplateVo coreGenerateTemplateVo : list) {
                FileTools.createDirs(packageDir + coreGenerateTemplateVo.getPath());
                FileUtil.writeUtf8String(coreGenerateTemplateVo.getData(), new File(packageDir + coreGenerateTemplateVo.getPath(), coreGenerateTemplateVo.getFileName()));
            }
            File zipFile = ZipUtil.zip(packageDir, tempDir + "package.zip");
        } else {
            // 同步
            if (!WebAppEnvs.get().envType.equals("dev")) throw new CommonException("只有在开发模式下才能进行同步代码");

            for (CoreGenerateTemplateVo coreGenerateTemplateVo : list) {
                if (coreGenerateTemplateVo.getType().equals("sql")) {
                    SQLScriptRunnerTools.execScript(coreGenerateTemplateVo.getData());
                } else {
                    FileUtil.writeUtf8String(coreGenerateTemplateVo.getData(), new File(WebAppEnvs.get().projectRoot + "/" + coreGenerateTemplateVo.getPath(), coreGenerateTemplateVo.getFileName()));
                }
            }
        }
    }

    @Override
    public List<GeneratePreviewVo> preview(Integer id) {

        GenerateTable generateTable = generateTableMapper.selectById(id);
        List<GenerateColumn> list = generateColumnMapper.selectList(new QueryWrapper<GenerateColumn>().eq("table_id", id));
        CoreGenerateService coreGenerateService = new CoreGenerateService();
        List<CoreGenerateTemplateVo> columnList = coreGenerateService.generateCode(generateTable, list);
        List<GeneratePreviewVo> voList = new ArrayList<>();
        for (CoreGenerateTemplateVo coreGenerateTemplateVo : columnList) {
            GeneratePreviewVo vo = new GeneratePreviewVo();
            vo.setName(coreGenerateTemplateVo.getFileName());
            vo.setType(coreGenerateTemplateVo.getType());
            vo.setContent(coreGenerateTemplateVo.getData());
            vo.setFileDir(coreGenerateTemplateVo.getPath() + "/");
            voList.add(vo);
        }
        return voList;

    }

    @Override
    public String getDbFieldType(String type) {
        type = getDbType(type);
        Map<String, String[]> map = SqlColumnEnum.getMap();
        String field = "";
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            if (Arrays.asList(entry.getValue()).contains(type)) {
                field = entry.getKey();
            }
        }
        if (field.equals("")) {
            field = "String";
        }
        return field;
    }

    @Override
    public String getDbType(String columnType) {
        if (StrUtil.contains(columnType, "(")) {
            return StrUtil.subAfter(columnType, "(", true);
        } else {
            return columnType;
        }
    }

    @Override
    public List<Map<String, Object>> tableList(String name, String comment) {

        String sql = "SHOW TABLE STATUS WHERE 1=1 ";
        if (!(name == null)) {
            sql += " AND Name like '%" + name + "%'";
        }
        if (!(comment == null)) {
            sql += " AND comment like '%" + comment + "%'";
        }
        List<Map<String, Object>> listData = jdbcTemplate.queryForList(sql);
        return listData;
    }

    @Override
    public Integer checkFile(String checkFile) {
        return null;
    }

    @Override
    public List<TableFiledVo> getTableColumn(String tableName) {
        return null;
    }

    /**
     * 获取表字段
     *
     * @param mapper
     * @return
     */
    @Override
    public Map<String, Map<String, Object>> getTableColumnByMapper(String mapper) {
        MapperInfoVo mapperInfo = CoreGenerateService.getMapperInfo(mapper);

        List<Map<String, Object>> columns = jdbcTemplate.queryForList("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE()) and TABLE_NAME='" + GlobalConfig.tablePrefix + mapperInfo.getTableName() + "'");

        Map<String, Map<String, Object>> tableColumn = new HashMap<>();
        for (Map<String, Object> column : columns) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", column.get("COLUMN_NAME"));
            item.put("comment", column.get("COLUMN_COMMENT"));
            item.put("default", column.get("COLUMN_DEFAULT"));
            item.put("primary", column.get("COLUMN_KEY").equals("PRI"));
            tableColumn.put((String) column.get("COLUMN_NAME"), item);
        }
        return tableColumn;
    }

    /**
     * 扫描指定包下的 Mapper 接口
     * @param addon 插件名称，如果是系统模块则为 "system"
     * @return Mapper 接口 Class 对象列表
     */
    public List<Class<? extends BaseMapper<?>>> getMappers(String addon) {
        List<Class<? extends BaseMapper<?>>> mapperClasses = new ArrayList<>();

        try {
            String basePackage = "system".equals(addon)
                    ? "com.niu.core.mapper"
                    : "com.niu." + addon + ".mapper";

            PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourceResolver);

            String packageSearchPath = "classpath*:" +
                    basePackage.replace('.', '/') +
                    "/**/*.class";

            org.springframework.core.io.Resource[] resources = resourceResolver.getResources(packageSearchPath);

            for (org.springframework.core.io.Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

                    // 检查是否是接口且有 @Mapper 注解
                    if (metadataReader.getClassMetadata().isInterface() &&
                            metadataReader.getAnnotationMetadata().hasAnnotation(Mapper.class.getName())) {

                        String className = metadataReader.getClassMetadata().getClassName();
                        Class<?> clazz = Class.forName(className);

                        // 检查是否继承自 BaseMapper 或 MPJBaseMapper
                        if (BaseMapper.class.isAssignableFrom(clazz) ||
                                MPJBaseMapper.class.isAssignableFrom(clazz)) {

                            // 安全的类型转换
                            mapperClasses.add((Class<? extends BaseMapper<?>>) clazz);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("扫描 Mapper 接口失败", e);
        }

        return mapperClasses;
    }
}
