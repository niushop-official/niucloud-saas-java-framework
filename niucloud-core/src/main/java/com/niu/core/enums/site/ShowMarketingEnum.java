package com.niu.core.enums.site;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.mapper.diy.DiyPageMapper;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.ShowMarketingVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
public class ShowMarketingEnum {

    private static final String MARKETING_TITLE = "营销活动";
    private static final String ADDON_TITLE = "营销工具";
    private static final String TOOL_TITLE = "系统工具";
    private static final String MARKETING_JSON_PATH = "loader/sys/show_marketing.json";
    private static final String APP_JSON_PATH = "loader/sys/show_app.json";

    private static ApplicationContext context;

    @Autowired
    private ISiteService siteService;
    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private DiyPageMapper diyPageMapper;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static Map<String, ShowMarketingVo> getShowMarketingTools() {
        ShowMarketingEnum instance = context.getBean(ShowMarketingEnum.class);
        Map<String, ShowMarketingVo> all = instance.getAllAddonAndTool();
        return Map.of("marketing", all.get("marketing"), "addon", all.get("addon"));
    }

    public Map<String, ShowMarketingVo> getAllAddonAndTool() {
        //获取所有show_marketing文件
        Map<String, ShowMarketingVo> marketingData = getMarketing();
        ShowMarketingVo marketingVo = marketingData.get("marketing");

        // 获取营销工具列表
        List<ShowMarketingVo.MarketingTool> marketingAddon = Optional.ofNullable(marketingData.get("tool"))
                .map(ShowMarketingVo::getList)
                .orElse(Collections.emptyList());

        // 构建基础结构
        Map<String, ShowMarketingVo> result = new HashMap<>();
        result.put("marketing", marketingVo);
        result.put("addon", createShowMarketingVo(ADDON_TITLE, marketingAddon));
        result.put("tool", createShowMarketingVo(TOOL_TITLE, new ArrayList<>()));

        // 处理应用数据
        Integer siteId = RequestUtils.siteId();
        List<String> addonModules = getSiteAddonModules(siteId);
        Map<String, List<ShowMarketingVo.MarketingTool>> appData = getShowAppData(addonModules, APP_JSON_PATH);

        Set<String> existingKeys = new HashSet<>();
        if (marketingVo != null && CollectionUtils.isNotEmpty(marketingVo.getList())) {
            marketingVo.getList().stream()
                    .filter(Objects::nonNull)
                    .map(ShowMarketingVo.MarketingTool::getKey)
                    .forEach(existingKeys::add);
        }

        // 填充应用数据到结果
        for (Map.Entry<String, List<ShowMarketingVo.MarketingTool>> entry : appData.entrySet()) {
            String categoryKey = entry.getKey();
            List<ShowMarketingVo.MarketingTool> items = entry.getValue();

            ShowMarketingVo categoryVo = result.get(categoryKey);
            if (categoryVo != null && CollectionUtils.isNotEmpty(items)) {
                for (ShowMarketingVo.MarketingTool item : items) {
                    if (item != null && !existingKeys.contains(item.getKey())) {
                        categoryVo.getList().add(item);
                        existingKeys.add(item.getKey());
                    }
                }
            }
        }

        // 处理站点插件
        processSiteAddons(siteId, result, existingKeys);

        return result;
    }

    private void processSiteAddons(Integer siteId, Map<String, ShowMarketingVo> result, Set<String> existingKeys) {
        List<Addon> siteAddons = siteService.getSiteAddons();
        if (CollectionUtils.isEmpty(siteAddons)) {
            return;
        }

        // 获取插件URL映射
        Map<String, String> addonUrls = getAddonUrls(siteAddons);

        // 过滤未存在的插件并转换为MarketingTool
        List<ShowMarketingVo.MarketingTool> remainingAddons = siteAddons.stream()
                .filter(Objects::nonNull)
                .filter(addon -> !existingKeys.contains(addon.getKey()))
                .map(addon -> {
                    ShowMarketingVo.MarketingTool tool = new ShowMarketingVo.MarketingTool();
                    tool.setTitle(addon.getTitle());
                    tool.setDesc(addon.getDesc());
                    tool.setIcon(addon.getIcon());
                    tool.setKey(addon.getKey());
                    tool.setUrl(StrUtil.isNotEmpty(addonUrls.get(addon.getKey())) ? "/" + addonUrls.get(addon.getKey()) : "");
                    return tool;
                })
                .collect(Collectors.toList());

        // 添加到addon列表
        ShowMarketingVo addonVo = result.get("addon");
        if (addonVo != null) {
            if (addonVo.getList() == null) {
                addonVo.setList(new ArrayList<>());
            }
            addonVo.getList().addAll(remainingAddons);

            // 特殊处理
            if (CollectionUtils.isNotEmpty(addonVo.getList())) {
                for (ShowMarketingVo.MarketingTool tool : addonVo.getList()) {
                    if ("pintuan_diy".equals(tool.getKey())) {
                        setUrl(tool, "DIY_PINTUAN_ACTIVITY_GOODS_DETAIL", siteId);
                    } else if ("seckill_diy".equals(tool.getKey())) {
                        setUrl(tool, "DIY_SECKILL_ACTIVITY_GOODS_DETAIL", siteId);
                    }
                }
            }
        }
    }

    private void setUrl(ShowMarketingVo.MarketingTool tool, String type, Integer siteId) {
        DiyPage diyPage = diyPageMapper.selectOne(new LambdaQueryWrapper<DiyPage>()
                .eq(DiyPage::getType, type)
                .eq(DiyPage::getIsDefault, 1)
                .eq(DiyPage::getMode, "diy")
                .eq(DiyPage::getSiteId, siteId)
                .last("limit 1"));
        if (diyPage != null && diyPage.getId() != null) {
            tool.setUrl("/site/decorate/edit?id=" + diyPage.getId() + "&back=/site/app/marketing&is_target=true");
        }
    }

    private Map<String, String> getAddonUrls(List<Addon> siteAddons) {
        List<String> addonKeys = siteAddons.stream()
                .map(Addon::getKey)
                .collect(Collectors.toList());

        List<SysMenu> sysMenus = sysMenuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .select(SysMenu::getRouterPath, SysMenu::getAddon)
                .in(SysMenu::getAddon, addonKeys)
                .eq(SysMenu::getIsShow, 1)
                .eq(SysMenu::getMenuType, 1)
                .orderByAsc(SysMenu::getId)
                .groupBy(SysMenu::getAddon));

        return sysMenus.stream()
                .collect(Collectors.toMap(SysMenu::getAddon, SysMenu::getRouterPath, (existing, replacement) -> existing));
    }

    public static Map<String, ShowMarketingVo> getMarketing() {
        Integer siteId = RequestUtils.siteId();
        List<String> addonModules = getSiteAddonModules(siteId);
        return parseMarketingToolsFromModules(addonModules, MARKETING_JSON_PATH);
    }

    private static Map<String, List<ShowMarketingVo.MarketingTool>> getShowAppData(List<String> addonModules, String path) {
        Map<String, List<ShowMarketingVo.MarketingTool>> appData = new HashMap<>();
        appData.put("app", new ArrayList<>());
        appData.put("tool", new ArrayList<>());
        appData.put("promotion", new ArrayList<>());

        for (String addonModule : addonModules) {
            try {
                String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, path);
                if (StrUtil.isNotEmpty(jsonString)) {
                    parseJsonToAppData(jsonString.trim(), appData);
                }
            } catch (Exception e) {
                log.error("获取show_app失败: {} => path: {}", addonModule, path, e);
            }
        }
        return appData;
    }

    private static void parseJsonToAppData(String jsonString, Map<String, List<ShowMarketingVo.MarketingTool>> appData) {
        if (!jsonString.startsWith("{")) return;

        try {
            JSONObject jsonObject = JSONUtil.parseObj(jsonString);
            parseJsonArrayToCategory(jsonObject, "app", appData.get("app"));
            parseJsonArrayToCategory(jsonObject, "tool", appData.get("tool"));
            parseJsonArrayToCategory(jsonObject, "promotion", appData.get("promotion"));
        } catch (Exception e) {
            log.error("解析JSON失败: {}", jsonString, e);
        }
    }

    private static void parseJsonArrayToCategory(JSONObject jsonObject, String categoryKey, List<ShowMarketingVo.MarketingTool> targetList) {
        if (jsonObject.containsKey(categoryKey)) {
            JSONArray jsonArray = jsonObject.getJSONArray(categoryKey);
            if (jsonArray != null && !jsonArray.isEmpty()) {
                List<ShowMarketingVo.MarketingTool> tools = JSONUtil.toList(jsonArray, ShowMarketingVo.MarketingTool.class);
                targetList.addAll(tools);
            }
        }
    }

    private static Map<String, ShowMarketingVo> parseMarketingToolsFromModules(List<String> addonModules, String path) {
        List<ShowMarketingVo.MarketingTool> appList = new ArrayList<>();
        List<ShowMarketingVo.MarketingTool> toolList = new ArrayList<>();
        List<ShowMarketingVo.MarketingTool> marketingList = new ArrayList<>();

        for (String addonModule : addonModules) {
            try {
                String jsonString = AddonModuleContext.readResourceAsStreamToText(addonModule, path);
                if (StrUtil.isNotEmpty(jsonString)) {
                    parseMarketingJson(jsonString.trim(), appList, toolList, marketingList);
                }
            } catch (Exception e) {
                log.error("获取marketing.json失败: {} => path: {}", addonModule, path, e);
            }
        }

        Map<String, ShowMarketingVo> result = new HashMap<>();
        result.put("app", createShowMarketingVo("应用", appList));
        result.put("tool", createShowMarketingVo("工具", toolList));
        result.put("marketing", createShowMarketingVo(MARKETING_TITLE, marketingList));

        return result;
    }

    private static void parseMarketingJson(String jsonString,
                                           List<ShowMarketingVo.MarketingTool> appList,
                                           List<ShowMarketingVo.MarketingTool> toolList,
                                           List<ShowMarketingVo.MarketingTool> marketingList) {
        if (!jsonString.startsWith("{")) return;

        try {
            JSONObject jsonObject = JSONUtil.parseObj(jsonString);
            parseJsonArrayToCategory(jsonObject, "app", appList);
            parseJsonArrayToCategory(jsonObject, "tool", toolList);
            parseJsonArrayToCategory(jsonObject, "marketing", marketingList);
        } catch (Exception e) {
            log.error("解析营销JSON失败: {}", jsonString, e);
        }
    }

    private static ShowMarketingVo createShowMarketingVo(String title, List<ShowMarketingVo.MarketingTool> list) {
        ShowMarketingVo vo = new ShowMarketingVo();
        vo.setTitle(title);
        vo.setList(list != null ? list : new ArrayList<>());
        return vo;
    }

    private static List<String> getSiteAddonModules(Integer siteId) {
        List<String> addonModules = new LinkedList<>();
        addonModules.add("core");

        if (ObjectUtil.isNull(siteId) || siteId == 0) {
            addonModules.addAll(SpringContext.bean(ICoreAddonService.class).getInstallAddonList().keySet());
        } else {
            addonModules.addAll(SpringContext.bean(ICoreSiteService.class).getAddonKeysBySiteId(siteId));
        }
        return addonModules;
    }
}