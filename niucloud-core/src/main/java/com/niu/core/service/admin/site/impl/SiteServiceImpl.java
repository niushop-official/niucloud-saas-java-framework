package com.niu.core.service.admin.site.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.google.gson.JsonArray;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.event.site.SiteEditAfterEvent;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.param.*;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteAdminVo;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.site.vo.SiteListVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.*;


/**
 * 站点实现
 */
@Service
public class SiteServiceImpl implements ISiteService {

    /** 站点tag */
    public static String cacheTagName = CacheTagEnum.SITE_CASH.getTagName();

    /** 是否使用缓存 */
    public static final boolean useCache = true;

    @Resource
    SiteMapper siteMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Resource
    SiteGroupMapper siteGroupMapper;

    @Resource
    ICoreSiteService iCoreSiteService;

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    ISiteGroupService siteGroupService;

    @Resource
    ISysUserService sysUserService;

    @Resource
    ISysUserRoleService userRoleService;

    @Resource
    ICoreSiteService coreSiteService;

    /**
     * 站点列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteListVo>
     */
    @Override
    public PageResult<SiteListVo> list(PageParam pageParam, SiteSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<Site> queryWrapper = new QueryWrapper<>();
        //查询条件判断组装
        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.like("site_name", searchParam.getKeywords()).or().like("keywords", searchParam.getKeywords());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("status", searchParam.getStatus());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getGroupId())) {
            queryWrapper.eq("group_id", searchParam.getGroupId());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getApp())) {
            queryWrapper.like("app", searchParam.getApp());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getSiteDomain())) {
            queryWrapper.like("site_domain", searchParam.getSiteDomain());
        }
        queryWrapper.ne("app_type", "admin");
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {

            String[] createTime = searchParam.getCreateTime();
            Long startTime = (createTime[0] == null) ? 0: DateUtils.StringToTimestamp(createTime[0]);
            System.out.println(startTime);
            Long endTime = (createTime[1] == null) ? 0: DateUtils.StringToTimestamp(createTime[1]);
            if(startTime > 0 && endTime > 0)
            {
                queryWrapper.between("create_time", startTime, endTime);
            }else if (startTime > 0 && endTime == 0)
            {
                queryWrapper.ge("create_time", startTime);
            }else if (startTime == 0 && endTime > 0)
            {
                queryWrapper.le("create_time", startTime);
            }
        }

        if (ObjectUtil.isNotEmpty(searchParam.getExpireTime())) {

            String[] expireTime = searchParam.getExpireTime();
            Long startTime = (expireTime[0] == null) ? 0: DateUtils.StringToTimestamp(expireTime[0]);
            System.out.println(startTime);
            Long endTime = (expireTime[1] == null) ? 0: DateUtils.StringToTimestamp(expireTime[1]);
            if(startTime > 0 && endTime > 0)
            {
                queryWrapper.between("expire_time", startTime, endTime);
            }else if (startTime > 0 && endTime == 0)
            {
                queryWrapper.ge("expire_time", startTime);
            }else if (startTime == 0 && endTime > 0)
            {
                queryWrapper.le("expire_time", startTime);
            }
        }


        queryWrapper.orderByDesc("create_time");

        IPage<Site> iPage = siteMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SiteListVo> list = new LinkedList<>();
        for (Site item : iPage.getRecords()) {
            SiteListVo vo = new SiteListVo();
            BeanUtils.copyProperties(item, vo);
            MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
            userRoleMPJQueryWrapper.select("nsu.uid, nsu.username, nsu.head_img, nsu.real_name, nsu.last_ip, nsu.last_time, nsu.create_time, nsu.login_count")
                    .setAlias("nsur")
                    .leftJoin("?_sys_user nsu ON nsur.uid = nsu.uid".replace("?_", GlobalConfig.tablePrefix));
            userRoleMPJQueryWrapper.eq("nsur.is_admin", 1);
            userRoleMPJQueryWrapper.eq("nsur.site_id", item.getSiteId());
            vo.setAdmin(sysUserRoleMapper.selectJoinOne(SiteAdminVo.class, userRoleMPJQueryWrapper));
            SiteGroup siteGroup=siteGroupMapper.selectById(item.getGroupId());
            vo.setGroupName(siteGroup.getGroupName());
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 站点详情
     * @param id 主键
     * @return SiteInfoVo
     */
    @Override
    public SiteInfoVo info(Integer id) {
        SiteInfoVo siteInfoVo=iCoreSiteService.getSiteCache(id);
        return siteInfoVo;
    }

    /**
     * 站点添加
     * @param addParam 参数
     */
    @Transactional
    @Override
    public void add(SiteAddParam addParam) {
        SiteGroup siteGroup=siteGroupService.info(addParam.getGroupId());
        if(ObjectUtil.isNull(siteGroup) || ObjectUtil.isEmpty(siteGroup)){
            throw new CommonException("SITE_GROUP_NOT_EXIST");
        }
        Site model = new Site();
        model.setSiteName(addParam.getSiteName());
        model.setGroupId(addParam.getGroupId());
        model.setAppType(AppTypeEnum.SITE.getName());
        model.setCreateTime(DateUtils.currTime());
        model.setExpireTime(DateUtils.StringToTimestamp(addParam.getExpireTime()));
        model.setApp(siteGroup.getApp());
        model.setAddons("");
        model.setSiteDomain(addParam.getSiteDomain());
        siteMapper.insert(model);
        Integer siteId=model.getSiteId();
        if(ObjectUtil.isNull(addParam.getUid()) || addParam.getUid()==0){
            //添加用户
            SiteUserParam siteUserParam=new SiteUserParam();
            siteUserParam.setUsername(addParam.getUsername());
            siteUserParam.setHeadImg("");
            siteUserParam.setStatus(1);
            siteUserParam.setRealName(addParam.getRealName());
            siteUserParam.setPassword(PasswordEncipher.encode(addParam.getPassword()));
            siteUserParam.setIsAdmin(1);
            sysUserService.addSiteUser(siteUserParam, siteId);
        }else{
            SysUserRoleParam sysUserRoleParam=new SysUserRoleParam();
            sysUserRoleParam.setUid(addParam.getUid());
            sysUserRoleParam.setRoleIds(new JsonArray().toString());
            sysUserRoleParam.setIsAdmin(1);
            sysUserRoleParam.setSiteId(siteId);
            userRoleService.add(sysUserRoleParam);
        }

        SiteAddAfterEvent event = new SiteAddAfterEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setName("SiteAddAfterEvent");
        event.setSite(model);
        event.setSiteGroup(siteGroup);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 站点编辑
     *
     * @param editParam 参数
     */
    @Transactional
    @Override
    public void edit(Integer id, SiteEditParam editParam) {
        Site model = siteMapper.selectOne(
                new QueryWrapper<Site>()
                    .eq("site_id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        if(ObjectUtil.isNotNull(editParam.getGroupId()) && ObjectUtil.isNotEmpty(editParam.getGroupId()) && editParam.getGroupId()!=model.getGroupId()){
            model.setGroupId(editParam.getGroupId());
        }
        if(ObjectUtil.isNotNull(editParam.getSiteName()) && ObjectUtil.isNotEmpty(editParam.getSiteName())){
            model.setSiteName(editParam.getSiteName());
        }

        SiteGroup oldSiteGroup=siteGroupService.info(model.getGroupId());


        JSONArray initallJsonArray=new JSONArray();
        if(ObjectUtil.isNull(model.getInitalledAddon()) || ObjectUtil.isEmpty(model.getInitalledAddon())){
            initallJsonArray.addAll(JSONUtil.parseArray(oldSiteGroup.getApp()));
            initallJsonArray.addAll(JSONUtil.parseArray(oldSiteGroup.getAddon()));
        }

        SiteGroup siteGroup=siteGroupService.info(editParam.getGroupId());
        if(ObjectUtil.isNull(siteGroup) || ObjectUtil.isEmpty(siteGroup)){
            throw new CommonException("SITE_GROUP_NOT_EXIST");
        }
        initallJsonArray.addAll(JSONUtil.parseArray(siteGroup.getApp()));
        initallJsonArray.addAll(JSONUtil.parseArray(siteGroup.getAddon()));

        model.setInitalledAddon(JSONUtil.toJsonStr(initallJsonArray));
        model.setSiteDomain(editParam.getSiteDomain());
        model.setExpireTime(new DateTime(editParam.getExpireTime()).getTime() / 1000);
        model.setStatus(model.getExpireTime()>DateUtils.currTime()?SiteStatusEnum.ON.getCode():SiteStatusEnum.EXPIRE.getCode());
        siteMapper.updateById(model);

        SiteEditAfterEvent event = new SiteEditAfterEvent();
        event.setSiteId(model.getSiteId());
        event.addAppSign("core");
        event.setName("SiteEditAfterEvent");
        event.setSite(model);
        event.setSiteGroup(siteGroup);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 站点删除
     * @param id 主键ID
     */
    @Transactional
    @Override
    public void del(Integer id) {
        Site model = siteMapper.selectOne(
                new QueryWrapper<Site>()
                    .eq("site_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setStatus(SiteStatusEnum.DELETE.getCode());
        siteMapper.updateById(model);
    }

    @Override
    public void closeSite(Integer siteId) {
        Site model = new Site();
        model.setSiteId(siteId);
        model.setStatus(SiteStatusEnum.CLOSE.getCode());
        siteMapper.updateById(model);
    }

    @Override
    public void openSite(Integer siteId) {
        Site model = new Site();
        model.setSiteId(siteId);
        model.setStatus(SiteStatusEnum.ON.getCode());
        siteMapper.updateById(model);
    }

    /**
     * 获取站点的菜单列表
     *
     * @param siteId
     * @param status
     * @return
     */
    @Override
    public Map<String, List<String>> getSiteApiList(Integer siteId, Integer status) {
        SiteInfoVo siteInfoVo= this.info(siteId);
        Map<String, List<String>> siteApi=new HashMap<>();
        if(ObjectUtil.isNull(siteInfoVo)){
            return siteApi;
        }
        String appType=siteInfoVo.getAppType();
        if(appType.equals(AppTypeEnum.ADMIN.getName())){
            return sysMenuService.getAllApiList(appType, status);
        }else{
            List<String> addonKeys= iCoreSiteService.getAddonKeysBySiteId(siteId);
            return sysMenuService.getApiListBySystem(appType, addonKeys);
        }
    }

    /**
     * 通过条件检索 站点数量
     *
     * @param siteSearchParam
     * @return
     */
    @Override
    public Integer getSiteCountByCondition(SiteSearchParam siteSearchParam) {
        QueryWrapper<Site> queryWrapper=new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(siteSearchParam.getCreateTime())) {

            String[] createTime = siteSearchParam.getCreateTime();
            Long startTime = (createTime[0] == null) ? 0: DateUtils.StringToTimestamp(createTime[0]);
            Long endTime = (createTime[1] == null) ? 0: DateUtils.StringToTimestamp(createTime[1]);
            if(startTime > 0 && endTime > 0)
            {
                queryWrapper.between("create_time", startTime, endTime);
            }else if (startTime > 0 && endTime == 0)
            {
                queryWrapper.ge("create_time", startTime);
            }else if (startTime == 0 && endTime > 0)
            {
                queryWrapper.le("create_time", startTime);
            }
        }

        if (ObjectUtil.isNotEmpty(siteSearchParam.getStatus())) {
            queryWrapper.eq("status", siteSearchParam.getStatus());
        }

        if (ObjectUtil.isNotEmpty(siteSearchParam.getGroupId())) {
            queryWrapper.eq("group_id", siteSearchParam.getGroupId());
        }

        if (ObjectUtil.isNotEmpty(siteSearchParam.getAppType())) {
            queryWrapper.eq("app_type", siteSearchParam.getAppType());
        }

        if (ObjectUtil.isNotEmpty(siteSearchParam.getExpireTime())) {

            String[] expireTime = siteSearchParam.getExpireTime();
            Long startTime = (expireTime[0] == null) ? 0: DateUtils.StringToTimestamp(expireTime[0]);
            Long endTime = (expireTime[1] == null) ? 0: DateUtils.StringToTimestamp(expireTime[1]);
            if(startTime > 0 && endTime > 0)
            {
                queryWrapper.between("expire_time", startTime, endTime);
            }else if (startTime > 0 && endTime == 0)
            {
                queryWrapper.ge("expire_time", startTime);
            }else if (startTime == 0 && endTime > 0)
            {
                queryWrapper.le("expire_time", startTime);
            }
        }
        Long siteCount=siteMapper.selectCount(queryWrapper);
        return siteCount.intValue();
    }

    /**
     * 获取站点的插件
     * @return
     */
    public List<Addon> getSiteAddons() {
        return addonMapper.selectList(new QueryWrapper<Addon>()
                .eq("type", AddonTypeEnum.ADDON.getType())
                .eq("status", 1)
                .in("`key`", coreSiteService.getAddonKeysBySiteId(RequestUtils.siteId())));
    }

    /**
     * 站点变更
     *
     * @param site
     * @param siteGroup
     */
    @Override
    @Async
    public void siteAddonChange(Site site, SiteGroup siteGroup) {
        SiteEditAfterEvent event = new SiteEditAfterEvent();
        event.setSiteId(site.getSiteId());
        event.addAppSign("core");
        event.setName("SiteEditAfterEvent");
        event.setSite(site);
        event.setSiteGroup(siteGroup);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

}
