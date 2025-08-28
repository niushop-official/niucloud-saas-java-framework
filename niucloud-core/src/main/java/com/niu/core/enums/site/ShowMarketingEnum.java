package com.niu.core.enums.site;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.site.vo.ShowMarketingVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;

import java.util.*;

@Log4j2
public class ShowMarketingEnum {
    public static Map<String, ShowMarketingVo> getShowMarketingTools() {
        Map<String, ShowMarketingVo> result = new HashMap<>();
        ShowMarketingVo showMarketingVo = new ShowMarketingVo();
        showMarketingVo.setTitle("营销活动");

        String path = "loader/sys/show_marketing.json";
        Integer siteId = RequestUtils.siteId();
        List<String> addonModules = getSiteAddonModules(siteId);

        List<ShowMarketingVo.MarketingTool> marketingTools = parseMarketingToolsFromModules(addonModules, path);
        if (marketingTools.isEmpty()) {
            showMarketingVo.setList(new ArrayList<>());
        } else {
            showMarketingVo.setList(marketingTools);
        }

        result.put("marketing", showMarketingVo);
        return result;
    }

    private static List<ShowMarketingVo.MarketingTool> parseMarketingToolsFromModules(List<String> addonModules, String path) {
        List<ShowMarketingVo.MarketingTool> marketingTools = new ArrayList<>();
        for (String addonModule : addonModules) {
            String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, path);
            if (StrUtil.isNotEmpty(jsonString)) {
                jsonString = jsonString.trim();
                try {
                    if (jsonString.startsWith("{")) {
                        JSONObject jsonObject = JSONUtil.parseObj(jsonString);
                        List<ShowMarketingVo.MarketingTool> marketing = JSONUtil.toList(jsonObject.getJSONArray("marketing"), ShowMarketingVo.MarketingTool.class);
                        marketingTools.addAll(marketing);
                    }
                } catch (Exception e) {
                    log.error("Error parsing JSON while reading resource from module: {} => path: {}", addonModule, path, e);
                }
            }
        }
        return marketingTools;
    }


    private static List<String> getSiteAddonModules(Integer siteId) {
        List<String> addonModules = new LinkedList<>();
        addonModules.add("core");
        if (ObjectUtil.isNull(siteId) || siteId == 0) {
            addonModules.addAll(SpringContext.bean(ICoreAddonService.class).getInstallAddonList().keySet().stream().toList());
        } else {
            addonModules.addAll(SpringContext.bean(ICoreSiteService.class).getAddonKeysBySiteId(siteId));
        }
        return addonModules;
    }
}
