package com.niu.core.service.admin.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy.DiyTheme;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.diy.DiyThemeMapper;
import com.niu.core.service.admin.diy.IDiyThemeService;
import com.niu.core.service.admin.diy.param.DiyThemeColorParam;
import com.niu.core.service.admin.diy.param.DiyThemeParam;
import com.niu.core.service.admin.diy.param.DiyThemeSetParam;
import com.niu.core.service.admin.diy.param.DiyThemeTitleParam;
import com.niu.core.service.admin.diy.vo.DiyThemeInfoVo;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.diy.ICoreDiyService;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiyThemeServiceImpl implements IDiyThemeService {

    @Resource
    private ICoreAddonService coreAddonService;

    @Resource
    private ICoreSiteService coreSiteService;

    @Resource
    private ICoreDiyService coreDiyService;

    @Resource
    private DiyThemeMapper diyThemeMapper;

    @Resource
    private AddonMapper addonMapper;

    @Override
    public JSONObject getDiyTheme() {
        Integer siteId = RequestUtils.siteId();
        SiteInfoVo siteCache = coreSiteService.getSiteCache(siteId);
        List<DiyTheme> themeDataList = diyThemeMapper.selectList(new QueryWrapper<DiyTheme>().eq("site_id", siteId).eq("type", "app").eq("is_selected", 1));
        Map<String, DiyTheme> themeData = new HashMap<>();
        if (!themeDataList.isEmpty()){
            themeData = themeDataList.stream().collect(Collectors.toMap(theme -> theme.getAddon(), theme -> theme));
        }

        JSONObject systemTheme = coreDiyService.getDefaultThemeColor("app");
        JSONObject appTheme = new JSONObject();
        JSONObject appThemeObj = new JSONObject();
        appThemeObj.set("id", ObjectUtil.isNotEmpty(themeData.get("app")) ? themeData.get("app").getId() : "");
        appThemeObj.set("icon", "");
        appThemeObj.set("addon_title", "系统");
        appThemeObj.set("title", ObjectUtil.isNotEmpty(themeData.get("app")) ? themeData.get("app").getTitle() : (!systemTheme.isEmpty() ? systemTheme.getJSONArray("theme_color").getJSONObject(0).get("title") : ""));

        Object themeValue = ObjectUtil.isNotEmpty(themeData.get("app")) ?
                themeData.get("app").getTheme() :
                (!systemTheme.isEmpty() ? systemTheme.getJSONArray("theme_color").getJSONObject(0).get("theme") : "");
        if (themeValue instanceof String) {
            appThemeObj.set("theme", JSONUtil.parseObj(themeValue));
        } else {
            appThemeObj.set("theme", themeValue);
        }

        appTheme.putOpt("app", appThemeObj);

        JSONObject data = new JSONObject();
        List<Addon> appsAndAddons = siteCache.getApps();
        appsAndAddons.addAll(siteCache.getSiteAddons());
        for (Addon value : appsAndAddons) {
            JSONObject addonTheme = coreDiyService.getDefaultThemeColor(value.getKey());
            if (!addonTheme.isEmpty() && addonTheme.containsKey("theme_color")) {
                JSONObject addonData = new JSONObject();
                addonData.set("id", ObjectUtil.isNotEmpty(themeData.get(value.getKey())) ? themeData.get(value.getKey()).getId() : "");
                addonData.set("icon", value.getIcon() != null ? value.getIcon() : "");
                addonData.set("addon_title", value.getTitle() != null ? value.getTitle() : "");
                addonData.set("title", ObjectUtil.isNotEmpty(themeData.get(value.getKey())) ? themeData.get(value.getKey()).getTitle() : addonTheme.getJSONArray("theme_color").getJSONObject(0).get("title"));

                Object addonThemeValue = ObjectUtil.isNotEmpty(themeData.get(value.getKey())) ?
                        themeData.get(value.getKey()).getTheme() :
                        addonTheme.getJSONArray("theme_color").getJSONObject(0).get("theme");
                if (addonThemeValue instanceof String) {
                    addonData.set("theme", JSONUtil.parseObj(addonThemeValue));
                } else {
                    addonData.set("theme", addonThemeValue);
                }

                data.putOpt(value.getKey(), addonData);
            }
        }

        if (data.isEmpty() || siteCache.getApps().size() > 1) {
            JSONObject mergedData = new JSONObject();
            for (String key : appTheme.keySet()) {
                mergedData.putOpt(key, appTheme.get(key));
            }

            for (String key : data.keySet()) {
                if (!mergedData.containsKey(key)) {
                    mergedData.putOpt(key, data.get(key));
                }
            }

            data = mergedData;
        }

        return data;
    }

    @Override
    @Transactional
    public void setDiyTheme(DiyThemeSetParam data) {
        Integer siteId = RequestUtils.siteId();
        DiyTheme diyTheme = diyThemeMapper.selectById(data.getId());
        if (ObjectUtil.isEmpty(diyTheme)) {
            throw new CommonException("主题色不存在");
        }
        List<Addon> addonData = addonMapper.selectList(new QueryWrapper<Addon>().eq("support_app", data.getAddon()));
        List<DiyTheme> addonSaveData = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(addonData)){
            for (Addon addon : addonData) {
                DiyTheme saveData = new DiyTheme();
                saveData.setSiteId(siteId);
                saveData.setType("addon");
                saveData.setAddon(addon.getKey());
                saveData.setTitle(diyTheme.getTitle());
                saveData.setTheme(data.getTheme().toString());
                saveData.setNewTheme(data.getNewTheme().toString());
                saveData.setIsSelected(1);
                saveData.setUpdateTime(System.currentTimeMillis() / 1000);
                addonSaveData.add(saveData);
            }
        }

        diyThemeMapper.update(null, new UpdateWrapper<DiyTheme>().eq("site_id",  siteId).eq("addon", data.getAddon()).eq("type", "addon").set("is_selected", 0));

        DiyTheme model = new DiyTheme();
        BeanUtils.copyProperties(data, model);
        model.setIsSelected(1);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        diyThemeMapper.updateById(model);

        if (!ObjectUtil.isNotEmpty(addonSaveData)) {
            for (DiyTheme saveData : addonSaveData) {

                diyThemeMapper.update(null, new UpdateWrapper<DiyTheme>().eq("site_id",  siteId).eq("addon", saveData.getAddon()).eq("type", "addon").set("is_selected", 0));
                diyThemeMapper.update(saveData, new UpdateWrapper<DiyTheme>().eq("site_id",  siteId).eq("addon", saveData.getAddon()).eq("type", "addon").eq("title", saveData.getTitle()));
            }
        }


    }

    @Override
    public List<DiyThemeInfoVo> getDefaultThemeColor(DiyThemeColorParam data) {
        String addon = data.getAddon();
        List<DiyTheme> themeList = diyThemeMapper.selectList(new QueryWrapper<DiyTheme>().eq("site_id", RequestUtils.siteId()).eq("addon", addon));
        List<DiyThemeInfoVo> voList = new ArrayList<>();
        for (DiyTheme theme : themeList) {
            DiyThemeInfoVo vo = new DiyThemeInfoVo();
            BeanUtils.copyProperties(theme, vo);
            JSONObject addonTheme = coreDiyService.getDefaultThemeColor(theme.getAddon());
            if (!addonTheme.isEmpty() && addonTheme.containsKey("theme_field")) {
                vo.setThemeField(addonTheme.getJSONArray("theme_field"));
            }
            voList.add(vo);
        }

        return voList;
    }

    @Override
    @Transactional
    public void addDiyTheme(DiyThemeParam data) {
        Integer siteId = RequestUtils.siteId();
        List<Addon> addonData = addonMapper.selectList(new QueryWrapper<Addon>().eq("support_app", data.getAddon()));
        List<DiyTheme> addonSaveData = new ArrayList<>();
        if (!addonData.isEmpty()) {
            for (Addon addon : addonData) {

                DiyTheme diyTheme = new DiyTheme();
                diyTheme.setSiteId(siteId);
                diyTheme.setType("addon");
                diyTheme.setAddon(addon.getKey());
                diyTheme.setTitle(data.getTitle());
                diyTheme.setTheme(data.getTheme().toString());
                diyTheme.setNewTheme(data.getNewTheme().toString());
                diyTheme.setDefaultTheme(data.getDefaultTheme().toString());
                diyTheme.setThemeType("diy");
                diyTheme.setCreateTime(System.currentTimeMillis() / 1000);
                addonSaveData.add(diyTheme);
            }
        }

        DiyTheme model = new DiyTheme();
        BeanUtils.copyProperties(data, model);
        model.setSiteId(siteId);
        model.setType("app");
        model.setThemeType("diy");
        model.setCreateTime(System.currentTimeMillis() / 1000);
        addonSaveData.add(model);
        if (ObjectUtil.isNotEmpty(addonSaveData)) {
            for (DiyTheme diyTheme : addonSaveData) {
                diyThemeMapper.insert(diyTheme);
            }
        }
    }

    @Override
    @Transactional
    public void editDiyTheme(Integer id, DiyThemeParam data) {
        DiyTheme diyThemeInfo = diyThemeMapper.selectOne(new QueryWrapper<DiyTheme>()
            .eq("site_id", RequestUtils.siteId())
            .eq("id", id));
        if (ObjectUtil.isEmpty(diyThemeInfo)) {
            throw new RuntimeException("主题色不存在");
        }
        List<DiyTheme> addonData = diyThemeMapper.selectList(new QueryWrapper<DiyTheme>().eq("site_id", RequestUtils.siteId()).eq("type", "addon").eq("title", diyThemeInfo.getTitle()));
        List<DiyTheme> addonSaveData = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(addonData)) {
            for (DiyTheme diyTheme : addonData) {
                DiyTheme saveData = new DiyTheme();
                saveData.setId(diyTheme.getId());
                saveData.setSiteId(diyTheme.getSiteId());
                saveData.setTitle(diyTheme.getTitle());
                saveData.setTheme(data.getTheme().toString());
                saveData.setNewTheme(data.getNewTheme().toString());
                saveData.setUpdateTime(System.currentTimeMillis() / 1000);

                addonSaveData.add(saveData);
            }
        }
        BeanUtils.copyProperties(data, diyThemeInfo);
        diyThemeInfo.setUpdateTime(System.currentTimeMillis() / 1000);

        diyThemeMapper.updateById(diyThemeInfo);

        if (ObjectUtil.isNotEmpty(addonSaveData)) {
            for (DiyTheme diyTheme : addonSaveData) {
                diyThemeMapper.updateById(diyTheme);
            }
        }
    }

    @Override
    public void delDiyTheme(Integer id) {
        DiyTheme diyThemeInfo = diyThemeMapper.selectOne(new QueryWrapper<DiyTheme>()
            .eq("site_id", RequestUtils.siteId())
            .eq("id", id));
        if (ObjectUtil.isEmpty(diyThemeInfo)) {
            throw new CommonException("主题色不存在");
        }
        if (diyThemeInfo.getThemeType().equals("default")) {
            throw new CommonException("默认主题色不能删除");
        }
        if (diyThemeInfo.getIsSelected().equals(1)) {
            throw new CommonException("主题配色已选中不能删除");
        }
        diyThemeMapper.deleteById(diyThemeInfo);
    }

    @Override
    public Boolean checkDiyThemeTitleUnique(DiyThemeTitleParam data) {
        QueryWrapper<DiyTheme> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", data.getTitle());
        queryWrapper.eq("addon", data.getAddon());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if(ObjectUtil.isNotEmpty(data.getId()))
        {
            queryWrapper.ne("id", data.getId());
        }
        return diyThemeMapper.selectCount(queryWrapper) == 0;
    }
}