package com.niu.core.enums.diy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.entity.addon.Addon;
import com.niu.core.service.admin.diy.param.TemplateParam;
import com.niu.core.service.core.site.ICoreSiteService;

import java.util.List;

public class TemplateEnum {

    private static ICoreSiteService coreSiteService() {
        return SpringContext.bean(ICoreSiteService.class);
    }

    /**
     * 获取微页面模板按插件分组
     *
     * @param siteId
     * @return
     */
    public static JSONObject getTemplateAddonGroup(Integer siteId){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();

        JSONObject template = new JSONObject();

        JSONObject systemTemplate = new JSONObject();
        systemTemplate.put("title", "系统");
        systemTemplate.put("key", "app");
        systemTemplate.put("list", jsonModuleLoader.getResultElement("core", "diy/template.json"));

        List<Addon> addons = coreSiteService().getSiteAddons(siteId);
        for (Addon addon : addons) {
            JSONObject addonTemplate = jsonModuleLoader.getResultElement(addon.getKey(), "diy/template.json");
            if (addonTemplate != null) {
                JSONObject templateItem = new JSONObject();
                templateItem.put("title", addon.getTitle());
                templateItem.put("key", addon.getKey());
                templateItem.put("list", addonTemplate);
                template.put(addon.getKey(), templateItem);
            }
        }

        return template;
    }

    /**
     * 获取全部微页面模板
     * @return
     */
    public static JSONObject getTemplate(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("diy/template.json");
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
        if (param.getAddon().isEmpty()) {
            diyTemplate = jsonModuleLoader.mergeResultElement("diy/template.json");
        } else {
            diyTemplate = jsonModuleLoader.getResultElement(param.getAddon(), "diy/template.json");
        }

        JSONObject template = new JSONObject();

        if (!param.getType().isEmpty()) {
            for (String key: diyTemplate.keySet()) {
                if (ObjectUtil.defaultIfNull(diyTemplate.getByPath(key + ".type", String.class), "").equals(param.getType())) {
                    template.put(key, diyTemplate.getJSONObject(key));
                }
            }
            return template;
        }
        if (!param.getAction().isEmpty()) {
            for (String key: diyTemplate.keySet()) {
                if (ObjectUtil.defaultIfNull(diyTemplate.getByPath(key + ".action", String.class), "").equals(param.getAction())) {
                    template.put(key, diyTemplate.getJSONObject(key));
                }
            }
            return template;
        }
        if (param.getKey() != null && param.getKey().length > 0) {
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
