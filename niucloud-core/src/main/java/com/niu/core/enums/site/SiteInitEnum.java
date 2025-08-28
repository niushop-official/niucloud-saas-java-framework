package com.niu.core.enums.site;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.service.admin.site.vo.ShowMarketingVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class SiteInitEnum {
    public static List<String> getSiteInitTables(Integer siteId) {
        List<String> result = new ArrayList<>();
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        List<String> addonModules = jsonModuleLoader.getSiteAddonModules(siteId);
        for (String addonModule : addonModules) {
            String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, "loader/" + "site/site_init.json");
            if (StrUtil.isNotEmpty(jsonString)){
                JSONArray tables = JSONUtil.parseArray(jsonString);
                if (!tables.isEmpty()){
                    tables.forEach(table -> {
                        result.add(GlobalConfig.tablePrefix + table);
                    });
                }
            }
        }
        return result;
    }
}
