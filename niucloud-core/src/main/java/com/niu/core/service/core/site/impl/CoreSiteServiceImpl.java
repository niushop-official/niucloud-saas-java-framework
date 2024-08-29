package com.niu.core.service.core.site.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.component.context.cache.CachedServiceImpl;
import com.niu.core.config.GlobalConfig;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.enums.addon.AddonActionEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.site.ICoreSiteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 站点服务层
 */
@Service
public class CoreSiteServiceImpl extends CachedServiceImpl<SiteMapper, Site> implements ICoreSiteService {

    @Resource
    SiteMapper siteMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    SiteGroupMapper siteGroupMapper;

    // 是否使用缓存
    private boolean cacheUsed = true;

    @Override
    public Site findUseCacheById(Integer siteId) {
        Site site = null;
        if (cacheUsed) {
            site = super.findUseCacheById(Site.class, siteId);
        } else {
            site = super.getById(siteId);
        }
        return site;
    }

    @Override
    public Site refreshCacheById(Integer siteId) {
        Site site = null;
        if (cacheUsed) {
            site = super.refreshCacheById(Site.class, siteId);
        } else {
            site = super.getById(siteId);
        }
        return site;
    }

    @Override
    public Site removeCacheById(Integer siteId) {
        Site site = null;
        if (cacheUsed) {
            site = super.removeCacheById(Site.class, siteId);
        } else {
            site = super.getById(siteId);
        }
        return site;
    }

    /**
     * 通过站点id获取支持的应用插件
     *
     * @param site_id
     */
    @Override
    public List<String> getAddonKeysBySiteId(Integer site_id) {
        Site siteInfo = siteMapper.selectById(site_id);
        if (ObjectUtil.isNull(siteInfo)) {
            return new ArrayList<String>();
        }
        //查询站点分组
        SiteGroup siteGroup = siteGroupMapper.selectById(siteInfo.getGroupId());
        JSONArray siteAddonJson = new JSONArray();
        if (!siteInfo.getAddons().equals("")) {
            JSONArray addonJson = JSONUtil.parseArray(siteInfo.getAddons());
            siteAddonJson.addAll(addonJson);
        }
        if (!siteInfo.getApp().equals("")) {
            JSONArray appJson = JSONUtil.parseArray(siteInfo.getApp());
            siteAddonJson.addAll(appJson);
        }
        if (ObjectUtil.isNotNull(siteGroup)) {
            if (!siteGroup.getApp().equals("")) {
                JSONArray groupAppJson = JSONUtil.parseArray(siteGroup.getApp());
                siteAddonJson.addAll(groupAppJson);
            }
            if (!siteGroup.getAddon().equals("")) {
                JSONArray groupAddonJson = JSONUtil.parseArray(siteGroup.getAddon());
                siteAddonJson.addAll(groupAddonJson);
            }
        }
        //去重
        List<String> list = JSONUtil.toList(siteAddonJson, String.class);
        return CollectionUtil.distinct(list);
    }

    /**
     * 站点详情
     *
     * @param siteId 主键ID
     * @return SiteInfoVo
     */
    @Override
    public SiteInfoVo getSiteCache(Integer siteId) {
        MPJQueryWrapper<Site> siteMPJQueryWrapper = new MPJQueryWrapper<>();
        siteMPJQueryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.create_time, ns.expire_time, ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, nsg.group_name")
                .setAlias("ns")
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix));
        siteMPJQueryWrapper.eq("ns.site_id", siteId);
        SiteInfoVo siteInfoVo = siteMapper.selectJoinOne(SiteInfoVo.class, siteMPJQueryWrapper);
        siteInfoVo.setAddonKeys(getAddonKeysBySiteId(siteInfoVo.getSiteId()));
        if (siteInfoVo.getAddonKeys().size() != 0) {
            siteInfoVo.setSiteAddons(addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", siteInfoVo.getAddonKeys()).eq("type", AddonActionEnum.ADDON.getCode())));
            siteInfoVo.setApps(addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", siteInfoVo.getAddonKeys()).eq("type", AddonActionEnum.APP.getCode())));
        } else {
            siteInfoVo.setSiteAddons(new ArrayList<>());
            siteInfoVo.setApps(new ArrayList<>());
        }
        return siteInfoVo;
    }

    @Override
    public List<Addon> getSiteAddons(Integer siteId) {
        return addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", getAddonKeysBySiteId(siteId)));
    }

}
