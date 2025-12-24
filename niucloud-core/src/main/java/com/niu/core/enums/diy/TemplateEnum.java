package com.niu.core.enums.diy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.entity.addon.Addon;
import com.niu.core.service.admin.diy.param.TemplateParam;
import com.niu.core.service.core.site.ICoreSiteService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateEnum {

    private static ICoreSiteService coreSiteService() {
        return SpringContext.bean(ICoreSiteService.class);
    }

    /**
     * 获取微页面模板和插件
     *
     * @param siteId
     * @return
     */
    public static Map<String, Map<String, Object>> getTemplateAddons(Integer siteId) {
        JsonModuleLoader jsonLoader = new JsonModuleLoader();
        Map<String, Map<String, Object>> templateList = new HashMap<>();

        // 加载系统核心模板
        JSONObject coreTemplates = jsonLoader.getResultElement("core", "diy/template.json");
        for (Map.Entry<String, Object> entry : coreTemplates.entrySet()) {
            Map<String, Object> template = (Map<String, Object>) entry.getValue();
            template.put("title", "系统");
            template.put("key", "app");
            template.put("type", entry.getKey());
            templateList.put(entry.getKey(), template);
        }

        // 加载插件模板
        List<Addon> siteAddons = coreSiteService().getSiteAddons(siteId);
        for (Addon addon : siteAddons) {
            JSONObject addonTemplates = jsonLoader.getResultElement(addon.getKey(), "diy/template.json");
            if (addonTemplates != null) {
                for (Map.Entry<String, Object> entry : addonTemplates.entrySet()) {
                    Map<String, Object> template = (Map<String, Object>) entry.getValue();
                    template.put("title", addon.getTitle());
                    template.put("key", addon.getKey());
                    template.put("type", entry.getKey());
                    templateList.put(entry.getKey(), template);
                }
            }
        }

        return templateList;
    }

    /**
     * 获取全部微页面模板
     * @return
     */
    public static JSONObject getTemplate(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/template.json");
    }

    /**
     * 根据筛选条件获取微页面模板
     *
     * @param param
     * @return
     */
    public static JSONObject getTemplate(TemplateParam param){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject diyTemplate = new JSONObject();
        if (ObjectUtil.isEmpty(param.getAddon())) {
            diyTemplate = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "diy/template.json");
        } else {
            diyTemplate = jsonModuleLoader.getResultElement(param.getAddon(), "diy/template.json");
        }

        JSONObject template = new JSONObject();

        if (ObjectUtil.isNotEmpty(param.getType())) {
            for (String key: diyTemplate.keySet()) {
                if (ObjectUtil.defaultIfNull(diyTemplate.getByPath(key + ".type", String.class), "").equals(param.getType())) {
                    template.put(key, diyTemplate.getJSONObject(key));
                }
            }
            return template;
        }
        if (ObjectUtil.isNotEmpty(param.getAction())) {
            for (String key: diyTemplate.keySet()) {
                if (ObjectUtil.defaultIfNull(diyTemplate.getByPath(key + ".action", String.class), "").equals(param.getAction())) {
                    template.put(key, diyTemplate.getJSONObject(key));
                }
            }
            return template;
        }
        if (ObjectUtil.isNotEmpty(param.getKey())) {
            for (String key: param.getKey()) {
                if (diyTemplate.getJSONObject(key) != null) {
                    template.put(key, diyTemplate.getJSONObject(key));
                }
            }
            return template;
        }

        return diyTemplate;
    }
}
