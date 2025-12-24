package com.niu.core.enums.diy;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.addon.Addon;
import com.niu.core.service.core.site.ICoreSiteService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LinkEnum {

    private static ICoreSiteService coreSiteService() {
        return SpringContext.bean(ICoreSiteService.class);
    }

    public static JSONObject getLink(){
        return getLink("");
    }

    public static JSONObject getLink(String addon){
        JSONObject link = new JSONObject();

        Map<String, Addon> siteAddons = coreSiteService().getSiteAddons(RequestUtils.siteId()).stream().collect(Collectors.toMap(Addon::getKey, i -> i));
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();

        if (addon.isEmpty()) {
            JsonModuleLoader.deepMerge(link, jsonModuleLoader.getResultElement("core", "diy/links.json"));
            for (String addonKey : siteAddons.keySet()) {
                JSONObject addonInfo = new JSONObject();
                addonInfo.set("key", siteAddons.get(addonKey).getKey());
                addonInfo.set("title", siteAddons.get(addonKey).getTitle());
                JSONObject addonLink = jsonModuleLoader.getResultElement(addonKey, "diy/links.json");
                for (String key : addonLink.keySet()) {
                    addonLink.putByPath(key + ".addon_info", addonInfo);
                }
                JsonModuleLoader.deepMerge(link, addonLink);
            }
        } else {
            JSONObject addonInfo = new JSONObject();
            addonInfo.set("key", siteAddons.get(addon).getKey());
            addonInfo.set("title", siteAddons.get(addon).getTitle());
            JSONObject addonLink = jsonModuleLoader.getResultElement(addon, "diy/links.json");
            for (String key : addonLink.keySet()) {
                addonLink.putByPath(key + ".addon_info", addonInfo);
            }
            JsonModuleLoader.deepMerge(link, addonLink);
        }
        return link;
    }

    public static JSONObject getAddonLink(){
        JSONObject link = new JSONObject();

        Map<String, Addon> siteAddons = coreSiteService().getSiteAddons(RequestUtils.siteId()).stream().collect(Collectors.toMap(Addon::getKey, i -> i));
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();

        JSONObject systemInfo = new JSONObject();
        systemInfo.set("key", "app");
        systemInfo.set("title", "系统");
        systemInfo.set("list", jsonModuleLoader.getResultElement("core", "diy/links.json"));
        link.put("app", systemInfo);

        for (String addonKey : siteAddons.keySet()) {
            JSONObject addonInfo = new JSONObject();
            addonInfo.set("key", siteAddons.get(addonKey).getKey());
            addonInfo.set("title", siteAddons.get(addonKey).getTitle());
            addonInfo.set("list", jsonModuleLoader.getResultElement(addonKey, "diy/links.json"));
            link.put(addonKey, addonInfo);
        }

        return link;
    }
}
