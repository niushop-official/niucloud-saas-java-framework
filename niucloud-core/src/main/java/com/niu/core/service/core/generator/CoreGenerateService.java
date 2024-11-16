package com.niu.core.service.core.generator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.generator.GenerateColumn;
import com.niu.core.entity.generator.GenerateTable;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.service.core.generator.vo.CoreGenerateColumnVo;
import com.niu.core.service.core.generator.vo.CoreGenerateTemplateVo;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.validation.annotation.Validated;

import java.io.StringWriter;
import java.util.*;

/**
 * 会员等级core服务层
 */
public class CoreGenerateService {

    /**
     * 初始化vm方法
     */
    public static void initVelocity() {
        try {
            //1.设置velocity的资源加载类
            Properties prop = new Properties();
            prop.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            prop.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            prop.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            Velocity.init(prop);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载velocity容器
     * @return VelocityContext
     */
    public static VelocityContext velocityContext (GenerateTable table, List<GenerateColumn> columns) {
        String lowerClassName = StrUtil.toCamelCase(table.getClassName());
        String className = StrUtil.upperFirst(lowerClassName);
        String addonName = table.getAddonName().equals("") ? "core" : table.getAddonName();
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("originalClassName", table.getClassName());
        velocityContext.put("tableName", table.getTableName());
        // 表名大驼峰
        velocityContext.put("humpTableName", StrUtil.upperFirst(StrUtil.toCamelCase(table.getTableName())));
        // 表名小驼峰
        velocityContext.put("lowerTableName", StrUtil.toCamelCase(table.getTableName()));
        velocityContext.put("tableContent", table.getTableContent());
        velocityContext.put("notes", table.getTableContent().endsWith("表") ? table.getTableContent().substring(0, table.getTableContent().length() - 1) : table.getTableContent());
        velocityContext.put("moduleName", table.getModuleName());
        velocityContext.put("className", className);
        velocityContext.put("lowerClassName", lowerClassName);
        velocityContext.put("addonName", addonName);
        velocityContext.put("orderType",  table.getOrderType());
        velocityContext.put("editType", table.getEditType());
        velocityContext.put("adminPath", table.getAddonName().equals("") ? "@/app" : "@/addon/" + table.getAddonName());
        velocityContext.put("primaryField", "id");
        velocityContext.put("sortColumn", "");
        List<CoreGenerateColumnVo> list = new ArrayList<>();
        Integer isSiteId = 0;
        for(GenerateColumn column : columns) {
            CoreGenerateColumnVo vo = new CoreGenerateColumnVo();
            if (column.getIsPk() == 1) {
                velocityContext.put("primaryField", column.getColumnName());
            }
            if (column.getIsOrder() == 1) {
                velocityContext.put("sortColumn", column.getColumnName());
            }
            //检测是否存在siteId
            if(column.getColumnName().equals("site_id")){
                isSiteId = 1;
            }
            BeanUtil.copyProperties(column, vo);
            vo.setLowerColumnFiledName(StrUtil.toCamelCase(column.getColumnName()));
            vo.setColumnFiledName(StrUtil.upperFirst(StrUtil.toCamelCase(column.getColumnName())));
            list.add(vo);
        }
        if(isSiteId == 1){
            velocityContext.put("isSiteId", 1);
        }else{
            velocityContext.put("isSiteId", 0);
        }

        velocityContext.put("columns", list);

        searchForm(velocityContext, columns);

        tableList(velocityContext, columns);

        editForm(velocityContext, columns);

        formValidate(velocityContext, columns);

        lang(velocityContext, columns);

        menuSql(table, velocityContext);

        return velocityContext;
    }

    /**
     * 生成搜索表单
     * @param velocityContext
     * @param columns
     * @return
     */
    public static void searchForm(VelocityContext velocityContext, List<GenerateColumn> columns) {
        String form = "";
        List<String> param = new LinkedList();

        for (GenerateColumn column : columns) {
            if (column.getIsPk().equals(1) || !column.getIsSearch().equals(1) || column.getColumnName().equals("site_id")) {
                continue;
            }

            velocityContext.put("column", column);
            velocityContext.put("lowerColumnName", StrUtil.toCamelCase(column.getColumnName()));

            String template = column.getViewType();
            if (column.getViewType().equals("radio") || column.getViewType().equals("select") || column.getViewType().equals("checkbox")) {
                template = "select";
            }
            if (column.getQueryType().equals("BETWEEN")) {
                template = column.getViewType().equals("datetime") ? "datetime" : "rangeInput";
            }

            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate("core/generator/vm/admin/search/" + template + ".vm", "UTF-8");
            tpl.merge(velocityContext, sw);
            form += sw.toString();

            if (column.getQueryType().equals("BETWEEN")) {
                param.add(column.getColumnName() + ": []");
            } else {
                param.add(column.getColumnName() + ": \"\"");
            }
        }

        velocityContext.put("searchParam", String.join(", \n", param));
        velocityContext.put("searchForm", form);
    }

    /**
     * 显示列表
     *
     * @param velocityContext
     * @param columns
     */
    public static void tableList(VelocityContext velocityContext, List<GenerateColumn> columns) {
        String table = "";

        for (GenerateColumn column : columns) {
            if (!column.getIsLists().equals(1) || column.getColumnName().equals("site_id")) {
                continue;
            }

            velocityContext.put("column", column);
            velocityContext.put("lowerColumnName", StrUtil.toCamelCase(column.getColumnName()));

            String template = "default";
            if (column.getViewType().equals("imageSelect")) {
                template = "image";
            }
            if (column.getViewType().equals("datetime")) {
                template = "datetime";
            }

            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate("core/generator/vm/admin/table/" + template + ".vm", "UTF-8");
            tpl.merge(velocityContext, sw);
            table += sw.toString();
        }

        velocityContext.put("listView", table);
    }

    /**
     * 生成编辑表单
     * @param velocityContext
     * @param columns
     * @return
     */
    public static void editForm(VelocityContext velocityContext, List<GenerateColumn> columns) {
        String form = "";
        List<String> param = new LinkedList();

        for (GenerateColumn column : columns) {
            if (column.getIsPk().equals(1)) {
                param.add(column.getColumnName() + ": 0");
            }
            if (column.getIsInsert().equals(0) || column.getIsUpdate().equals(0) || column.getColumnName().equals("site_id")) {
                continue;
            }

            velocityContext.put("column", column);
            velocityContext.put("lowerColumnName", StrUtil.toCamelCase(column.getColumnName()));

            String template = column.getViewType();
            if (column.getViewType().equals("radio") || column.getViewType().equals("select") || column.getViewType().equals("checkbox")) {
            }

            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate("core/generator/vm/admin/form/" + template + ".vm", "UTF-8");
            tpl.merge(velocityContext, sw);
            form += sw.toString();

            param.add(column.getColumnName() + ": \"\"");
        }

        velocityContext.put("formData", String.join(", \n", param));
        velocityContext.put("formView", form);
    }

    /**
     * 表单验证规则
     * @param velocityContext
     * @param columns
     */
    public static void formValidate(VelocityContext velocityContext, List<GenerateColumn> columns) {
        List<String> validate = new LinkedList<>();

        for (GenerateColumn column : columns) {
            if (column.getIsPk().equals(1) || column.getColumnName().equals("site_id")) {
                continue;
            }

            String lowerColumnName = StrUtil.toCamelCase(column.getColumnName());

            // 验证规则
            List<String> validateRule = new LinkedList<>();
            if (column.getIsRequired().equals(1)) validateRule.add("{ required: true, message: t('"+ lowerColumnName +"Placeholder'), trigger: 'blur' }");

            if (!column.getValidateType().isEmpty()) {
                if (JSONUtil.isJson(column.getValidateType())) {
                    JSONArray validateType = JSONUtil.parseArray(column.getValidateType());
                    JSONArray limit = validateType.getJSONArray(1);
                    switch (validateType.getStr(0, "")) {
                        case "min":
                            validateRule.add("{ validator: (rule: any, value: string, callback: any) => {" +
                                    "value += ''\n" +
                                    "        if (value.length < "+ limit.getInt(0) +") {\n" +
                                    "            callback(new Error(t('generateMin') + "+ limit.getInt(0) +" + t('generateUnit')))\n" +
                                    "        } else {\n" +
                                    "            callback()\n" +
                                    "        }" +
                                    "}, trigger: 'blur' }");
                            break;
                        case "max":
                            validateRule.add("{ validator: (rule: any, value: string, callback: any) => {" +
                                    "value += ''\n" +
                                    "        if (value.length > "+ limit.getInt(0) +") {\n" +
                                    "            callback(new Error(t('generateMax') + "+ limit.getInt(0) +" + t('generateUnit')))\n" +
                                    "        } else {\n" +
                                    "            callback()\n" +
                                    "        }" +
                                    "}, trigger: 'blur' }");
                            break;
                        case "between":
                            validateRule.add("{ validator: (rule: any, value: string, callback: any) => {" +
                                    "value += ''\n" +
                                    "        if (value.length < "+ limit.getInt(0) +") {\n" +
                                    "           callback(new Error(t('generateMin') + "+ limit.getInt(0) +" + t('generateUnit')))\n" +
                                    "        } else if (value.length > "+ limit.getInt(1) +") {\n" +
                                    "           callback(new Error(t('generateMax') + "+ limit.getInt(0) +" + t('generateUnit')))\n" +
                                    "        } else {\n" +
                                    "            callback()\n" +
                                    "        }" +
                                    "}, trigger: 'blur' }");
                            break;
                    }
                } else {
                    validateRule.add("{ validator: Validate."+ column.getValidateType() +"Verify, trigger: 'blur' }");
                }
            }

            if (validateRule.size() > 0) validate.add(column.getColumnName() + ": [\n" + String.join(", \n", validateRule) + "\n]");
        }

        velocityContext.put("formValidate", String.join(", \n", validate));
    }

    /**
     * admin 端语言包
     *
     * @param velocityContext
     * @param columns
     */
    public static void lang(VelocityContext velocityContext, List<GenerateColumn> columns) {
        JSONObject lang = new JSONObject();

        String className = (String) velocityContext.get("className");
        String lowerClassName = (String) velocityContext.get("lowerClassName");
        String notes = (String) velocityContext.get("notes");

        lang.set("add" + className, "添加" + notes);
        lang.set("update" + className, "编辑" + notes);
        lang.set(lowerClassName + "DeleteTips", "確定要刪除" + notes + "吗？");

        for (GenerateColumn column : columns) {
            if (column.getIsPk().equals(1) || column.getColumnName().equals("site_id")) continue;
            lang.set(StrUtil.toCamelCase(column.getColumnName()), column.getColumnComment());
            if (column.getIsInsert().equals(1) || column.getIsUpdate().equals(1)) {
                String prefix = "请输入";
                if (column.getViewType().equals("radio") || column.getViewType().equals("select") || column.getViewType().equals("checkbox")) {
                    prefix = "请选择";
                }
                if (column.getViewType().equals("imageSelect") || column.getViewType().equals("videoSelect") || column.getViewType().equals("fileSelect")) {
                    prefix = "请上传";
                }
                lang.set(StrUtil.toCamelCase(column.getColumnName()) + "Placeholder", prefix + column.getColumnComment());
            }
        }
        velocityContext.put("lang", lang.toJSONString(1, null));
    }

    /**
     * 菜单sql
     * @param velocityContext
     */
    public static void menuSql(GenerateTable table, VelocityContext velocityContext) {
        String menuTable = "?_sys_menu".replace("?_", GlobalConfig.tablePrefix);
        String notes = (String) velocityContext.get("notes");
        String className = (String) velocityContext.get("originalClassName");

        List<String> sql = new LinkedList<>();

        HashMap<String, String> menuKey = new HashMap<>();
        menuKey.put("root", "'" + table.getTableName() + "'");
        menuKey.put("list", "'" + table.getTableName() + "_list'");
        menuKey.put("add", "'" + table.getTableName() + "_add'");
        menuKey.put("edit", "'" + table.getTableName() + "_edit'");
        menuKey.put("delete", "'" + table.getTableName() + "_del'");
        menuKey.put("edit_page", "'" + table.getTableName() + "_edit_page'");

        String apiUrl = velocityContext.get("addonName").toString() + "/" + velocityContext.get("moduleName").toString() + "/" + velocityContext.get("tableName").toString();

        sql.add("DELETE FROM "+ menuTable + " WHERE menu_key in (" + String.join(", ", menuKey.values()) + ");");

        sql.add("INSERT INTO "+ menuTable + "(app_type, menu_name, menu_short_name, menu_key, parent_key, menu_type, icon, api_url, router_path, view_path, methods, sort, status, is_show, addon, source) VALUES\n" +
                "('site', '"+ notes +"', '"+ notes +"', "+ menuKey.get("root") + ", '"+ table.getParentMenu() +"', 0, '', '', '', '', '', 100, 1, 1, '"+ table.getAddonName() +"', 'generator');");

        String routerPath = (table.getAddonName().equals("") ? "" : table.getAddonName() + "/") + table.getModuleName() + (className.equals("") ? "" : "/" + className);
        String viewPath = table.getModuleName() +"/"+ table.getClassName();
        sql.add("INSERT INTO "+ menuTable + "(app_type, menu_name, menu_short_name, menu_key, parent_key, menu_type, icon, api_url, router_path, view_path, methods, sort, status, is_show, addon, source) VALUES\n" +
                "('site', '"+ notes +"列表', '', "+ menuKey.get("list") + ", "+ menuKey.get("root") +", 1, '', '"+ apiUrl + "', '"+ routerPath +"', '"+ viewPath +"', 'get', 100, 1, 1, '"+ table.getAddonName() +"', 'generator');");

        sql.add("INSERT INTO "+ menuTable + "(app_type, menu_name, menu_short_name, menu_key, parent_key, menu_type, icon, api_url, router_path, view_path, methods, sort, status, is_show, addon, source) VALUES\n" +
                "('site', '"+ notes +"添加', '', "+ menuKey.get("add") + ", "+ menuKey.get("root") +", 2, '', '"+ apiUrl + "', '', '', 'post', 90, 1, 1, '"+ table.getAddonName() +"', 'generator');");

        sql.add("INSERT INTO "+ menuTable + "(app_type, menu_name, menu_short_name, menu_key, parent_key, menu_type, icon, api_url, router_path, view_path, methods, sort, status, is_show, addon, source) VALUES\n" +
                "('site', '"+ notes +"编辑', '', "+ menuKey.get("edit") + ", "+ menuKey.get("root") +", 2, '', '"+ apiUrl + "/<id>', '', '', 'put', 80, 1, 1, '"+ table.getAddonName() +"', 'generator');");

        sql.add("INSERT INTO "+ menuTable + "(app_type, menu_name, menu_short_name, menu_key, parent_key, menu_type, icon, api_url, router_path, view_path, methods, sort, status, is_show, addon, source) VALUES\n" +
                "('site', '"+ notes +"删除', '', "+ menuKey.get("delete") + ", "+ menuKey.get("root") +", 2, '', '"+ apiUrl +"/<id>', '', '', 'delete', 70, 1, 1, '"+ table.getAddonName() +"', 'generator');");

        if (table.getEditType().equals(2)) {
            String editRouterPath = routerPath + "_edit";
            String editViewPath = table.getModuleName() +"/"+ table.getClassName() + "_edit";
            sql.add("INSERT INTO "+ menuTable + "(app_type, menu_name, menu_short_name, menu_key, parent_key, menu_type, icon, api_url, router_path, view_path, methods, sort, status, is_show, addon, source) VALUES\n" +
                    "('site', '"+ notes +"添加/编辑', '"+ notes +"', "+ menuKey.get("edit_page") + ", "+ menuKey.get("root") +", 1, '', '"+ apiUrl + "/<id>', '"+ editRouterPath +"', '"+ editViewPath +"', 'get', 100, 1, 0, '"+ table.getAddonName() +"', 'generator');");
        }

        velocityContext.put("sql", String.join("\n", sql));
    }

    /**
     * 加载所有模板以及文件
     * @param table
     * @return
     */
    public static List<CoreGenerateTemplateVo> template(GenerateTable table) {

        String addonName = table.getAddonName().equals("") ? "core" : table.getAddonName();
        String lowerClassName = StrUtil.toCamelCase(table.getClassName());
        String className = StrUtil.upperFirst(lowerClassName);
        String javaPath = "niucloud-core/src/main/java/com/niu/core";
        if (!addonName.equals("core")) {
            javaPath = "niucloud-addon/"+ addonName +"/src/main/java/com/niu/" + addonName;
        }

        String adminVueAddonName = table.getAddonName().equals("") ? "app" : table.getAddonName();

        String adminVuePath = "";
        if(adminVueAddonName.equals("app"))
        {
            adminVuePath = "admin/src/" + adminVueAddonName;
        }else{
            adminVuePath = "admin/src/addon/" + adminVueAddonName;
        }

        List<CoreGenerateTemplateVo> list = new LinkedList<>();

        //控制器添加
        CoreGenerateTemplateVo controller = new CoreGenerateTemplateVo();
        controller.setTemplate("java/controller.vm");
        controller.setFileName(className + "Controller.java");
        controller.setType("java");
        controller.setPath(javaPath + "/controller/adminapi/" + table.getModuleName());
        list.add(controller);

        //实体添加
        CoreGenerateTemplateVo entity = new CoreGenerateTemplateVo();
        entity.setTemplate("java/entity.vm");
        entity.setFileName(StrUtil.upperFirst(StrUtil.toCamelCase(table.getTableName())) + ".java");
        entity.setType("java");
        entity.setPath(javaPath + "/entity/" + table.getModuleName());
        list.add(entity);

        //mapper添加
        CoreGenerateTemplateVo mapper = new CoreGenerateTemplateVo();
        mapper.setTemplate("java/mapper.vm");
        mapper.setFileName(StrUtil.upperFirst(StrUtil.toCamelCase(table.getTableName())) + "Mapper.java");
        mapper.setType("java");
        mapper.setPath(javaPath + "/mapper/" + table.getModuleName());
        list.add(mapper);

        //service添加
        CoreGenerateTemplateVo service = new CoreGenerateTemplateVo();
        service.setTemplate("java/service.vm");
        service.setFileName("I" + className + "Service.java");
        service.setType("java");
        service.setPath(javaPath + "/service/admin/" + table.getModuleName());
        list.add(service);

        //serviceImpl添加
        CoreGenerateTemplateVo serviceImpl = new CoreGenerateTemplateVo();
        serviceImpl.setTemplate("java/serviceimpl.vm");
        serviceImpl.setFileName(className + "ServiceImpl.java");
        serviceImpl.setType("java");
        serviceImpl.setPath(javaPath + "/service/admin/" + table.getModuleName() + "/impl");
        list.add(serviceImpl);

        //paramEdit添加
        CoreGenerateTemplateVo paramEdit = new CoreGenerateTemplateVo();
        paramEdit.setTemplate("java/paramEdit.vm");
        paramEdit.setFileName(className + "Param.java");
        paramEdit.setType("java");
        paramEdit.setPath(javaPath + "/service/admin/" + table.getModuleName() + "/param");
        list.add(paramEdit);

        //paramSearch添加
        CoreGenerateTemplateVo paramSearch = new CoreGenerateTemplateVo();
        paramSearch.setTemplate("java/paramSearch.vm");
        paramSearch.setFileName(className + "SearchParam.java");
        paramSearch.setType("java");
        paramSearch.setPath(javaPath + "/service/admin/" + table.getModuleName() + "/param");
        list.add(paramSearch);

        //voInfo添加
        CoreGenerateTemplateVo voInfo = new CoreGenerateTemplateVo();
        voInfo.setTemplate("java/voInfo.vm");
        voInfo.setFileName(className + "InfoVo.java");
        voInfo.setType("java");
        voInfo.setPath(javaPath + "/service/admin/" + table.getModuleName() + "/vo");
        list.add(voInfo);

        //voList添加
        CoreGenerateTemplateVo voList = new CoreGenerateTemplateVo();
        voList.setTemplate("java/voList.vm");
        voList.setFileName(className + "ListVo.java");
        voList.setType("java");
        voList.setPath(javaPath + "/service/admin/" + table.getModuleName() + "/vo");
        list.add(voList);

        //VUE adminapi添加
        CoreGenerateTemplateVo adminAPI = new CoreGenerateTemplateVo();
        adminAPI.setTemplate("admin/api.vm");
        adminAPI.setFileName(table.getClassName() + ".ts");
        adminAPI.setType("vue_admin");
        adminAPI.setPath(adminVuePath + "/api/" + table.getModuleName());
        list.add(adminAPI);

        //VUE lang添加
        CoreGenerateTemplateVo adminLang = new CoreGenerateTemplateVo();
        adminLang.setTemplate("admin/lang.vm");
        adminLang.setFileName(table.getModuleName() + "." + table.getClassName() + ".json");
        adminLang.setType("vue_admin");
        adminLang.setPath(adminVuePath + "/lang/zh-cn");
        list.add(adminLang);

        //VUE view index
        CoreGenerateTemplateVo adminView = new CoreGenerateTemplateVo();
        adminView.setTemplate("admin/index.vm");
        adminView.setFileName(table.getClassName() + ".vue");
        adminView.setType("vue_admin");
        adminView.setPath(adminVuePath + "/views/" + table.getModuleName());
        list.add(adminView);

        //VUE view edit
        if (table.getEditType().equals(2)) {
            CoreGenerateTemplateVo adminViewEdit = new CoreGenerateTemplateVo();
            adminViewEdit.setTemplate("admin/edit_page.vm");
            adminViewEdit.setFileName(table.getClassName() + "_edit.vue");
            adminViewEdit.setType("vue_admin");
            adminViewEdit.setPath(adminVuePath + "/views/" + table.getModuleName());
            list.add(adminViewEdit);

            //VUE lang添加
            CoreGenerateTemplateVo editPageLang = new CoreGenerateTemplateVo();
            editPageLang.setTemplate("admin/lang.vm");
            editPageLang.setFileName(table.getModuleName() + "." + table.getClassName() + "_edit.json");
            editPageLang.setType("vue_admin");
            editPageLang.setPath(adminVuePath + "/lang/zh-cn");
            list.add(editPageLang);
        } else {
            //VUE view edit弹框
            CoreGenerateTemplateVo adminViewEditComp = new CoreGenerateTemplateVo();
            adminViewEditComp.setTemplate("admin/edit.vm");
            adminViewEditComp.setFileName(table.getClassName().toLowerCase().replace("_", "-") + "-edit.vue");
            adminViewEditComp.setType("vue_admin");
            adminViewEditComp.setPath(adminVuePath + "/views/" + table.getModuleName() + "/components");
            list.add(adminViewEditComp);
        }

        // 菜单
        CoreGenerateTemplateVo menuSql = new CoreGenerateTemplateVo();
        menuSql.setTemplate("sql/menu_sql.vm");
        menuSql.setFileName("site_menu.sql");
        menuSql.setType("sql");
        menuSql.setPath("sql");
        list.add(menuSql);

        for (CoreGenerateTemplateVo coreGenerateTemplateVo : list) {
            coreGenerateTemplateVo.setAddon(addonName);
        }

        return list;
    }

    /**
     * 生成对应的code代码
     * @param table
     * @param columns
     * @return
     */
    public List<CoreGenerateTemplateVo> generateCode(GenerateTable table, List<GenerateColumn> columns) {
        if (table.getModuleName().isEmpty()) {
            table.setTableName(StrUtil.upperFirst(StrUtil.toCamelCase(table.getTableContent())));
        }

        initVelocity();

        VelocityContext context = velocityContext(table, columns);

        List<CoreGenerateTemplateVo> list = template(table);

        for (CoreGenerateTemplateVo coreGenerateTemplateVo : list) {
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate("core/generator/vm/" + coreGenerateTemplateVo.getTemplate(), "UTF-8");
            tpl.merge(context, sw);
            coreGenerateTemplateVo.setData(sw.toString());
        }

        return list;
    }


}
