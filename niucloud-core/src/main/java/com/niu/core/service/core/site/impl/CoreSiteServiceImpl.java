package com.niu.core.service.core.site.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.component.context.cache.CachedServiceImpl;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteAddonInitRecord;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.addon.AddonActionEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteAddonInitRecordMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.site.vo.SiteInfoCacheVo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 站点服务层
 */
@Service
public class CoreSiteServiceImpl extends CachedServiceImpl<SiteMapper, Site> implements ICoreSiteService {

    /**
     * 站点tag
     */
    public static String cacheTagName = CacheTagEnum.SITE_CACHE.getTagName();

    /**
     * 是否使用缓存
     */
    public static final boolean useCache = true;

    @Resource
    private Cached cached;

    @Resource
    SiteMapper siteMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    IAddonService addonService;

    @Resource
    SiteGroupMapper siteGroupMapper;

    @Resource
    SiteAddonInitRecordMapper siteAddonInitRecordMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 通过站点id获取支持的应用插件
     *
     * @param siteId
     */
    @Override
    public List<String> getAddonKeysBySiteId(Integer siteId) {

        return cached.rememberObject(useCache, cacheTagName + "_" + siteId, Arrays.asList("getAddonKeysBySiteId", siteId), uniqueKey -> {

            Site siteInfo = siteMapper.selectById(siteId);
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

        SiteInfoCacheVo siteCache = cached.rememberObject(useCache, cacheTagName + "_" + siteId, Arrays.asList("getSiteCache", siteId), uniqueKey -> {
            MPJQueryWrapper<Site> siteMPJQueryWrapper = new MPJQueryWrapper<>();
            siteMPJQueryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.create_time, ns.expire_time, ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, nsg.group_name")
                    .setAlias("ns")
                    .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix));
            siteMPJQueryWrapper.eq("ns.site_id", siteId);

            return siteMapper.selectJoinOne(SiteInfoCacheVo.class, siteMPJQueryWrapper);

        });
        SiteInfoVo siteInfoVo = new SiteInfoVo();
        if (ObjectUtil.isNotEmpty(siteCache)) {
            BeanUtil.copyProperties(siteCache, siteInfoVo);
            siteInfoVo.setAddonKeys(getAddonKeysBySiteId(siteInfoVo.getSiteId()));
            if (siteInfoVo.getAddonKeys().size() != 0) {
                siteInfoVo.setSiteAddons(addonService.getAddonListByKeys(siteInfoVo.getAddonKeys(), ""));
                siteInfoVo.setApps(addonService.getAddonListByKeys(siteInfoVo.getAddonKeys(), AddonActionEnum.APP.getCode()));
            }else{
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
     */
    @Override
    public void siteExpireClose() {
        Site model = new Site();
        model.setStatus(SiteStatusEnum.EXPIRE.getCode());
        siteMapper.update(model, new QueryWrapper<Site>()
                .gt("expire_time", 0)
                .lt("expire_time", System.currentTimeMillis() / 1000)
                .ne("status", SiteStatusEnum.EXPIRE.getCode())
        );
    }

    /**
     * 清除站点缓存
     */
    @Override
    public void clearSiteCache(Integer siteId) {
        cached.tag(cacheTagName + "_" + siteId).clear();
    }

    @Override
    public Boolean siteInitBySiteId(Integer siteId, List<String> tables) {
        List<Integer> userIds = new ArrayList<>();
        try {
            for (String table : tables) {
                if (!isTableExists(table)) {
                    continue;
                }

                if ("nc_sys_user_role".equals(table)) {
                    LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<SysUserRole>()
                            .eq(SysUserRole::getSiteId, siteId)
                            .eq(SysUserRole::getIsAdmin, 0)
                            .select(SysUserRole::getUid);

                    userIds = sysUserRoleMapper.selectList(queryWrapper).stream()
                            .map(SysUserRole::getUid)
                            .collect(Collectors.toList());

                    if (!userIds.isEmpty()) {
                        sysUserRoleMapper.delete(queryWrapper);
                    }
                }
                else if ("nc_sys_user".equals(table)) {
                    if (!userIds.isEmpty()) {
                        sysUserMapper.deleteByIds(userIds);
                    }
                }
                else {
                    jdbcTemplate.update("DELETE FROM " + table + " WHERE site_id = ?", siteId);
                }
            }

            Site site = siteMapper.selectById(siteId);
            SiteGroup siteGroup = siteGroupMapper.selectById(site.getGroupId());

            SiteAddAfterEvent event = new SiteAddAfterEvent();
            event.setSiteId(siteId);
            event.addAppSign("core");
            event.setName("SiteAddAfterEvent");
            event.setSite(site);
            event.setSiteGroup(siteGroup);
            EventAndSubscribeOfPublisher.publishAll(event);

            // 清除缓存
            cached.getAllKeys().stream().forEach(key -> cached.remove(key));
            return true;
        } catch (Exception e) {
            throw new CommonException("站点初始化失败: " + e.getMessage());
        }
    }

    private boolean isTableExists(String tableName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.TABLES " +
                            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?",
                    Integer.class,
                    tableName
            ) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
