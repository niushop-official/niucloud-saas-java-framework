package com.niu.core.service.core.site.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.component.context.cache.CachedServiceImpl;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteAddonInitRecord;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.enums.addon.AddonActionEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteAddonInitRecordMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.site.vo.SiteInfoCacheVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 站点服务层
 */
@Service
public class CoreSiteServiceImpl extends CachedServiceImpl<SiteMapper, Site> implements ICoreSiteService {

    /** 站点tag */
    public static String cacheTagName = CacheTagEnum.SITE_CASH.getTagName();

    /** 是否使用缓存 */
    public static final boolean useCache = true;

    @Resource
    private Cached cached;

    @Resource
    SiteMapper siteMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    SiteGroupMapper siteGroupMapper;

    @Resource
    SiteAddonInitRecordMapper siteAddonInitRecordMapper;

    @Override
    public Site findUseCacheById(Integer siteId) {
        Site site = null;
        if (useCache) {
            site = super.findUseCacheById(Site.class, siteId);
        } else {
            site = super.getById(siteId);
        }
        return site;
    }

    @Override
    public Site refreshCacheById(Integer siteId) {
        Site site = null;
        if (useCache) {
            site = super.refreshCacheById(Site.class, siteId);
        } else {
            site = super.getById(siteId);
        }
        return site;
    }

    @Override
    public Site removeCacheById(Integer siteId) {
        Site site = null;
        if (useCache) {
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

        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("getAddonKeysBySiteId", site_id), uniqueKey -> {

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
        });


    }

    /**
     * 站点详情
     *
     * @param siteId 主键ID
     * @return SiteInfoVo
     */
    @Override
    public SiteInfoVo getSiteCache(Integer siteId) {

        SiteInfoCacheVo cache = cached.rememberObject(useCache, cacheTagName, Arrays.asList("getSiteCache", siteId), uniqueKey ->{
            MPJQueryWrapper<Site> siteMPJQueryWrapper = new MPJQueryWrapper<>();
            siteMPJQueryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.create_time, ns.expire_time, ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, nsg.group_name")
                    .setAlias("ns")
                    .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix));
            siteMPJQueryWrapper.eq("ns.site_id", siteId);

            SiteInfoCacheVo siteCache = siteMapper.selectJoinOne(SiteInfoCacheVo.class, siteMPJQueryWrapper);
            return siteCache;
        });
        SiteInfoVo siteInfoVo = new SiteInfoVo();
/*        if(ObjectUtil.isNotEmpty(cache))
        {
            BeanUtil.copyProperties(cache, siteInfoVo);
            JSONArray addonKeys = JSONUtil.parseArray(cache.getStr("addonKeys"));
            siteInfoVo.setAddonKeys(addonKeys.toList(String.class));
            if (siteInfoVo.getAddonKeys().size() != 0) {
                JSONArray siteAddons = JSONUtil.parseArray(cache.getStr("siteAddons"));
                JSONArray apps = JSONUtil.parseArray(cache.getStr("apps"));
                siteInfoVo.setSiteAddons(siteAddons.toList(Addon.class));
                siteInfoVo.setApps(apps.toList(Addon.class));
            } else {
                siteInfoVo.setSiteAddons(new ArrayList<>());
                siteInfoVo.setApps(new ArrayList<>());
            }
        }*/
        if(ObjectUtil.isNotEmpty(cache))
        {
            BeanUtil.copyProperties(cache, siteInfoVo);
            siteInfoVo.setAddonKeys(getAddonKeysBySiteId(siteInfoVo.getSiteId()));
            if (siteInfoVo.getAddonKeys().size() != 0) {
                siteInfoVo.setSiteAddons(addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", siteInfoVo.getAddonKeys()).eq("type", AddonActionEnum.ADDON.getCode())));
                siteInfoVo.setApps(addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", siteInfoVo.getAddonKeys()).eq("type", AddonActionEnum.APP.getCode())));
            } else {
                siteInfoVo.setSiteAddons(new ArrayList<>());
                siteInfoVo.setApps(new ArrayList<>());
            }
        }

        return siteInfoVo;
    }

    @Override
    public List<Addon> getSiteAddons(Integer siteId) {
        return addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", getAddonKeysBySiteId(siteId)));
    }

    /**
     * 站点插件是否已执行初始化
     *
     * @param siteId
     * @param addon
     * @return
     */
    @Override
    public boolean siteAddonIsInit(Integer siteId, String addon) {
        Long count = siteAddonInitRecordMapper.selectCount(new QueryWrapper<SiteAddonInitRecord>().eq("site_id", siteId).eq("addon", addon));
        return count == 1 ? true : false;
    }

    /**
     * 添加站点插件初始化记录
     *
     * @param siteId
     * @param addon
     */
    @Override
    public void addSiteAddonInitRecord(Integer siteId, String addon) {
        SiteAddonInitRecord model = new SiteAddonInitRecord();
        model.setSiteId(siteId);
        model.setAddon(addon);
        siteAddonInitRecordMapper.insert(model);
    }

    /**
     * 站点到期关闭
     *
     */
    @Override
    public void siteExpireClose() {
        Site model = new Site();
        model.setStatus(SiteStatusEnum.EXPIRE.getCode());
        siteMapper.update(model, new QueryWrapper<Site>()
                .gt("expire_time",0)
                .lt("expire_time", System.currentTimeMillis() / 1000)
                .ne("status", SiteStatusEnum.EXPIRE.getCode())
        );
    }

    /**
     * 清除站点缓存
     */
    @Override
    public void clearSiteCache() {
        cached.tag(cacheTagName).clear();
    }
}
