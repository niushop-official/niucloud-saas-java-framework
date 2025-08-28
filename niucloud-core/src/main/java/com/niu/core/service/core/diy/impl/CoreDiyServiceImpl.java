package com.niu.core.service.core.diy.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy.DiyTheme;
import com.niu.core.enums.diy.ThemeColorEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.diy.DiyThemeMapper;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.diy.ICoreDiyService;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CoreDiyServiceImpl implements ICoreDiyService {

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    DiyThemeMapper diyThemeMapper;

    @Resource
    private ICoreSiteService coreSiteService;

    @Resource
    private AddonMapper addonMapper;

    @Override
    public JSONArray getThemeColorDict() {
        return ThemeColorEnum.getThemeColor();
    }

    @Override
    public JSONObject getDefaultThemeColor(String addon) {
        return ThemeColorEnum.getAddonThemeColor(addon);
    }


    @Override
    public void initDefaultDiyTheme(Integer siteId) {
        SiteInfoVo siteCache = coreSiteService.getSiteCache(siteId);
        JSONArray themeColorList = ThemeColorEnum.getThemeColor();

        if (themeColorList == null || themeColorList.isEmpty()) {
            return;
        }

        List<DiyTheme> diyThemes = new ArrayList<>();

        // 处理主题配置
        themeColorList.forEach(item -> {
            JSONObject themeColor = JSONUtil.parseObj(item);
            String themeType = themeColor.getStr("theme_type");
            JSONArray colors = themeColor.getJSONArray("theme_color");

            if (colors == null || colors.isEmpty()) {
                return;
            }

            // 处理应用主题
            if ("app_theme".equals(themeType)) {
                addAppThemes(diyThemes, siteId, "app", "app", colors);
                return;
            }

            // 处理其他应用和插件主题
            siteCache.getApps().stream()
                    .filter(app -> (themeType + "_theme").equals(app.getKey()))
                    .forEach(app -> {
                        // 添加应用主题
                        addAppThemes(diyThemes, siteId, "app", app.getKey(), colors);

                        // 添加关联插件主题
                        List<Addon> addons = addonMapper.selectList(
                                new LambdaQueryWrapper<Addon>()
                                        .eq(Addon::getSupportApp, app.getKey()));

                        if (CollectionUtil.isNotEmpty(addons)) {
                            addons.forEach(addon ->
                                    addAppThemes(diyThemes, siteId, "addon", addon.getKey(), colors));
                        }
                    });
        });

        if (CollectionUtil.isNotEmpty(diyThemes)) {
            insertNewThemes(diyThemes, siteId);
        }
    }

    private void addAppThemes(List<DiyTheme> themes, Integer siteId,
                              String type, String addonKey, JSONArray colors) {
        for (int i = 0; i < colors.size(); i++) {
            JSONObject themeObj = colors.getJSONObject(i);
            themes.add(new DiyTheme().builder()
                    .siteId(siteId)
                    .type(type)
                    .addon(addonKey)
                    .title(themeObj.getStr("title"))
                    .theme(themeObj.getStr("theme"))
                    .defaultTheme(themeObj.getStr("theme"))
                    .themeType("default")
                    .isSelected(i == 0 ? 1 : 0)
                    .createTime(DateUtils.currTime()).build());
        }
    }

    private void insertNewThemes(List<DiyTheme> themes, Integer siteId) {
        Set<String> existThemeKeys = diyThemeMapper.selectList(
                        new LambdaQueryWrapper<DiyTheme>()
                                .eq(DiyTheme::getSiteId, siteId)
                                .in(DiyTheme::getAddon,
                                        themes.stream().map(DiyTheme::getAddon).distinct().collect(Collectors.toList()))
                                .in(DiyTheme::getTitle,
                                        themes.stream().map(DiyTheme::getTitle).distinct().collect(Collectors.toList())))
                .stream()
                .map(t -> t.getAddon() + "|" + t.getTitle())
                .collect(Collectors.toSet());

        List<DiyTheme> newThemes = themes.stream()
                .filter(t -> !existThemeKeys.contains(t.getAddon() + "|" + t.getTitle()))
                .collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(newThemes)) {
            diyThemeMapper.insert(newThemes);
        }
    }
}
