package com.niu.core.service.admin.generator;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.generator.param.GenerateCodeParam;
import com.niu.core.service.admin.generator.param.GenerateEditParam;
import com.niu.core.service.admin.generator.param.GenerateParam;
import com.niu.core.service.admin.generator.param.GenerateSearchParam;
import com.niu.core.service.admin.generator.vo.*;
import com.niu.core.service.core.generator.vo.CoreGenerateTemplateVo;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器接口服务类
 */
public interface IGenerateService {


    PageResult<GenerateListVo> getPage(PageParam pageParam, GenerateSearchParam searchParam);

    /**
     * 代码生成详情
     *
     * @param id 主键ID
     * @return GenerateDetailVo
     */
    GenerateDetailVo getInfo(Integer id);

    /**
     * 代码生成新增
     *
     * @param generateParam 参数
     */
    Integer add(GenerateParam generateParam);

    /**
     * 代码生成器编辑
     * @param id
     * @param generateParam
     */
    void edit(Integer id, GenerateEditParam generateParam);

    /**
     * 代码生成删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 生成代码包
     * @param generateCodeParam
     * @return
     */
    void generate(GenerateCodeParam generateCodeParam);

    /**
     * 预览
     * @param id
     */
    List<GeneratePreviewVo>  preview(Integer id);

    /**
     * 获取字段类型
     * @param type
     * @return
     */
    String getDbFieldType(String type);

    /**
     * 获取数据表字段类型
     * @param type
     * @return
     */
    String getDbType(String type);

    /**
     * 数据表列表
     * @param name
     * @param comment
     * @return
     */
    List<Map<String, Object>> tableList(String name, String comment);

    /**
     * 检测文件存在性
     * @param checkFile
     * @return
     */
    Integer checkFile(String checkFile);

    /**
     * 获取表字段
     * @param tableName
     * @return
     */
    List<TableFiledVo> getTableColumn(String tableName);

    /**
     * 获取模型
     * @return
     */
    List<String> getModels();

    /**
     * 获取表字段
     * @param model
     * @return
     */
    TableFiledVo getModelColumn(String model);




}
