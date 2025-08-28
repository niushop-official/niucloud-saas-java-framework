package com.niu.core.service.admin.home.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.ImageToBase64ConverterUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.entity.user.UserCreateSiteLimit;
import com.niu.core.enums.addon.AddonActionEnum;
import com.niu.core.enums.addon.AddonStatusEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.mapper.user.UserCreateSiteLimitMapper;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.admin.addon.vo.AddonInfoVo;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.home.IAuthSiteService;
import com.niu.core.service.admin.home.param.HomeSiteAddParam;
import com.niu.core.service.admin.home.param.SiteParam;
import com.niu.core.service.admin.home.param.SiteSearchParam;
import com.niu.core.service.admin.home.vo.SiteGroupVo;
import com.niu.core.service.admin.home.vo.SiteInfoVo;
import com.niu.core.service.admin.home.vo.SiteListVo;
import com.niu.core.service.admin.home.vo.UserCreateSiteVo;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.SiteAddParam;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.core.site.ICoreSiteService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthSiteServiceImpl implements IAuthSiteService {

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    ICoreSiteService iCoreSiteService;

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    IAuthService authService;

    @Resource
    SiteGroupMapper siteGroupMapper;

    @Resource
    UserCreateSiteLimitMapper userCreateSiteLimitMapper;

    @Resource
    IAddonService addonService;

    @Resource
    ISiteGroupService siteGroupService;

    @Resource
    ISiteService siteService;
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
            queryWrapper.like("site_name", searchParam.getKeywords()).or().like("site_id", searchParam.getKeywords());
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

        List<Integer> siteIds = getSiteIds();
        if(!authService.isSuperAdmin()) {
            if (ObjectUtil.isNotEmpty(siteIds)) {
                queryWrapper.in("site_id", siteIds);
            } else {
                return PageResult.build(page, limit, 0).setData(new ArrayList<>());
            }
        }

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

        if(ObjectUtil.isEmpty(searchParam.getSort())){
            queryWrapper.orderByDesc("create_time");
        }else{
            queryWrapper.orderByDesc(searchParam.getSort());
        }

        IPage<Site> iPage = siteMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SiteListVo> list = new LinkedList<>();
        for (Site item : iPage.getRecords()) {
            SiteListVo vo = new SiteListVo();
            BeanUtils.copyProperties(item, vo);
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
        MPJQueryWrapper<Site> siteMPJQueryWrapper = new MPJQueryWrapper<>();
        siteMPJQueryWrapper.select("ns.site_id, ns.site_name, ns.group_id, ns.keywords, ns.app_type, ns.logo, ns.`desc`, ns.status, ns.latitude, ns.longitude, ns.province_id, ns.city_id, ns.district_id, ns.address, ns.full_address, ns.phone, ns.business_hours, ns.create_time, ns.expire_time, ns.front_end_name, ns.front_end_logo, ns.front_end_icon, ns.icon, ns.member_no, ns.app, ns.addons, ns.initalled_addon, ns.site_domain, nsg.group_name")
                .setAlias("ns")
                .leftJoin("?_site_group nsg ON ns.group_id = nsg.group_id".replace("?_", GlobalConfig.tablePrefix));
        siteMPJQueryWrapper.eq("ns.site_id", id);
        SiteInfoVo siteInfoVo = siteMapper.selectJoinOne(SiteInfoVo.class, siteMPJQueryWrapper);
        siteInfoVo.setAddonKeys(iCoreSiteService.getAddonKeysBySiteId(siteInfoVo.getSiteId()));
        if(siteInfoVo.getAddonKeys().size()!=0){
            siteInfoVo.setSiteAddons(addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", siteInfoVo.getAddonKeys()).eq("type", AddonActionEnum.ADDON.getCode())));
            siteInfoVo.setApps(addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", siteInfoVo.getAddonKeys()).eq("type", AddonActionEnum.APP.getCode())));
        }else{
            siteInfoVo.setSiteAddons(new ArrayList<>());
            siteInfoVo.setApps(new ArrayList<>());
        }
        return siteInfoVo;
    }

    /**
     * 站点添加
     * @param addParam 参数
     */
    @Override
    public void add(SiteParam addParam) {
        Site model = new Site();
        model.setSiteName(addParam.getSiteName());
        model.setGroupId(addParam.getGroupId());
        model.setKeywords(addParam.getKeywords());
        model.setAppType(addParam.getAppType());
        //model.setLogo(UrlUtils.toRelativeUrl(addParam.getLogo()));
        model.setDesc(addParam.getDesc());
        model.setStatus(addParam.getStatus());
        model.setLatitude(addParam.getLatitude());
        model.setLongitude(addParam.getLongitude());
        model.setProvinceId(addParam.getProvinceId());
        model.setCityId(addParam.getCityId());
        model.setDistrictId(addParam.getDistrictId());
        model.setAddress(addParam.getAddress());
        model.setFullAddress(addParam.getFullAddress());
        model.setPhone(addParam.getPhone());
        model.setBusinessHours(addParam.getBusinessHours());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setExpireTime(addParam.getExpireTime());
        model.setFrontEndName(addParam.getFrontEndName());
        model.setFrontEndLogo(addParam.getFrontEndLogo());
        model.setFrontEndIcon(addParam.getFrontEndIcon());
        model.setIcon(addParam.getIcon());
        model.setMemberNo(addParam.getMemberNo());
        model.setApp(addParam.getApp());
        model.setAddons(addParam.getAddons());
        model.setInitalledAddon(addParam.getInitalledAddon());
        model.setSiteDomain(addParam.getSiteDomain());
        siteMapper.insert(model);
    }

    /**
     * 站点编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, SiteParam editParam) {
        Site model = siteMapper.selectOne(
                new QueryWrapper<Site>()
                        .eq("site_id",  id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setSiteId(id);
        model.setSiteName(editParam.getSiteName());
        model.setGroupId(editParam.getGroupId());
        model.setKeywords(editParam.getKeywords());
        model.setAppType(editParam.getAppType());
        //model.setLogo(UrlUtils.toRelativeUrl(editParam.getLogo()));
        model.setDesc(editParam.getDesc());
        model.setStatus(editParam.getStatus());
        model.setLatitude(editParam.getLatitude());
        model.setLongitude(editParam.getLongitude());
        model.setProvinceId(editParam.getProvinceId());
        model.setCityId(editParam.getCityId());
        model.setDistrictId(editParam.getDistrictId());
        model.setAddress(editParam.getAddress());
        model.setFullAddress(editParam.getFullAddress());
        model.setPhone(editParam.getPhone());
        model.setBusinessHours(editParam.getBusinessHours());
        model.setExpireTime(editParam.getExpireTime());
        model.setFrontEndName(editParam.getFrontEndName());
        model.setFrontEndLogo(editParam.getFrontEndLogo());
        model.setFrontEndIcon(editParam.getFrontEndIcon());
        model.setIcon(editParam.getIcon());
        model.setMemberNo(editParam.getMemberNo());
        model.setApp(editParam.getApp());
        model.setAddons(editParam.getAddons());
        model.setInitalledAddon(editParam.getInitalledAddon());
        model.setSiteDomain(editParam.getSiteDomain());
        siteMapper.updateById(model);
    }

    /**
     * 站点删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Site model = siteMapper.selectOne(
                new QueryWrapper<Site>()
                        .eq("site_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        siteMapper.delete(new QueryWrapper<Site>().eq("site_id", id));
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
     * 获取用户的
     *
     * @return
     */
    @Override
    public List<Integer> getSiteIds() {
        MPJQueryWrapper<SysUserRole> queryWrapper = new MPJQueryWrapper<SysUserRole>();
        queryWrapper.select("site_id").eq("uid", RequestUtils.uid()).ne("site_id", RequestUtils.defaultSiteId()).eq("status", 1);
        List<SysUserRole> sysUserRoleList=sysUserRoleMapper.selectList(queryWrapper);
        List<Integer> siteIds=new ArrayList<>();
        for (SysUserRole sysUserRole:sysUserRoleList) {
            siteIds.add(sysUserRole.getSiteId());
        }
        return siteIds;
    }

    /**
     * 获取可选择的店铺套餐
     *
     * @return
     */
    @Override
    public List<UserCreateSiteVo> getSiteGroup() {
        List<UserCreateSiteVo> userCreateSiteVoList=new ArrayList<>();
        if(authService.isSuperAdmin()){
            List<SiteGroup> siteGroupList=siteGroupMapper.selectList(new QueryWrapper<SiteGroup>());
            for (SiteGroup siteGroup:siteGroupList) {
                UserCreateSiteVo userCreateSiteVo=new UserCreateSiteVo();
                userCreateSiteVo.setGroupId(siteGroup.getGroupId());
                SiteGroupVo siteGroupVo=new SiteGroupVo();
                BeanUtil.copyProperties(siteGroup, siteGroupVo);
                siteGroupVo.setAppName(addonService.getTitleListByKey(siteGroup.getApp()));
                siteGroupVo.setAddonName(addonService.getTitleListByKey(siteGroup.getAddon()));
                userCreateSiteVo.setSiteGroup(siteGroupVo);
                userCreateSiteVoList.add(userCreateSiteVo);
            }
        }else{
            QueryWrapper<UserCreateSiteLimit> userCreateSiteLimitQueryWrapper=new QueryWrapper<>();
            userCreateSiteLimitQueryWrapper.eq("uid", RequestUtils.uid());
            List<UserCreateSiteLimit> userCreateSiteLimitList=userCreateSiteLimitMapper.selectList(userCreateSiteLimitQueryWrapper);
            for (UserCreateSiteLimit userCreateSiteLimit: userCreateSiteLimitList) {
                UserCreateSiteVo userCreateSiteVo=new UserCreateSiteVo();
                BeanUtil.copyProperties(userCreateSiteLimit, userCreateSiteVo);
                SiteGroup siteGroup=siteGroupMapper.selectById(userCreateSiteLimit.getGroupId());
                SiteGroupVo siteGroupVo=new SiteGroupVo();
                BeanUtil.copyProperties(siteGroup, siteGroupVo);
                siteGroupVo.setAppName(addonService.getTitleListByKey(siteGroup.getApp()));
                siteGroupVo.setAddonName(addonService.getTitleListByKey(siteGroup.getAddon()));
                userCreateSiteVo.setSiteGroup(siteGroupVo);
                userCreateSiteVoList.add(userCreateSiteVo);
            }
        }
        return userCreateSiteVoList;
    }

    /**
     * 用户添加站点
     *
     * @param homeSiteAddParam
     */
    @Override
    public void createSite(HomeSiteAddParam homeSiteAddParam) {
        Integer month=1;
        if(!authService.isSuperAdmin()){
            UserCreateSiteLimit userCreateSiteLimit=userCreateSiteLimitMapper.selectOne(new QueryWrapper<UserCreateSiteLimit>().eq("uid", RequestUtils.uid()).eq("group_id", homeSiteAddParam.getGroupId()));
            Assert.notNull(userCreateSiteLimit, "NO_PERMISSION_TO_CREATE_SITE_GROUP");
            long userSiteNum=siteGroupService.getUserSiteGroupSiteNum(RequestUtils.uid(), homeSiteAddParam.getGroupId());
            if(userSiteNum>userCreateSiteLimit.getNum()-1){
                throw new CommonException("SITE_GROUP_CREATE_SITE_EXCEEDS_LIMIT");
            }
            month=userCreateSiteLimit.getMonth();
        }
        SiteAddParam siteAddParam=new SiteAddParam();
        siteAddParam.setSiteName(homeSiteAddParam.getSiteName());
        siteAddParam.setUid(RequestUtils.uid());
        siteAddParam.setGroupId(homeSiteAddParam.getGroupId());
        siteAddParam.setExpireTime(DateUtils.getDateAddMonth(month));
        siteService.add(siteAddParam);
    }

    @Override
    public List<AddonInfoVo> getSiteGroupAppList() {
        List<String> siteGroupAppList = getSiteGroupApps();
        if (CollectionUtils.isEmpty(siteGroupAppList)){
            return List.of();
        }
        List<Addon> addonList = addonMapper.selectList(new LambdaQueryWrapper<Addon>()
                .eq(Addon::getStatus, AddonStatusEnum.ON.getCode())
                .eq(Addon::getType, AddonActionEnum.APP.getCode())
                .in(Addon::getKey, siteGroupAppList));

        return processAddonList(addonList);
    }

    private List<AddonInfoVo> processAddonList(List<Addon> addonList){
        List<AddonInfoVo> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(addonList)){
            return result;
        }
        for (Addon addon : addonList) {
            AddonInfoVo addonInfoVo = new AddonInfoVo();
            BeanUtils.copyProperties(addon, addonInfoVo);
            addonInfoVo.setStatusName(AddonStatusEnum.getName(addon.getStatus()));
            try {
                addonInfoVo.setIcon(ImageToBase64ConverterUtil.convertToBase64(addon.getIcon()));
            } catch (IOException e) {
                addonInfoVo.setIcon("");
            }
            result.add(addonInfoVo);
        }
        return result;
    }

    private List<String> getSiteGroupApps() {
        List<SiteGroup> siteGroups;
        if(authService.isSuperAdmin()){
            siteGroups = siteGroupMapper.selectList(new QueryWrapper<SiteGroup>().select("app"));
        }else {
            MPJQueryWrapper<UserCreateSiteLimit> mpjqw = new MPJQueryWrapper<>();
            mpjqw.setAlias("ucsl").leftJoin("?_site_group sg on ucsl.group_id = sg.group_id".replace("?_", GlobalConfig.tablePrefix));
            mpjqw.select("sg.group_id, sg.app");
            mpjqw.eq("ucsl.uid", RequestUtils.uid());
            siteGroups = userCreateSiteLimitMapper.selectJoinList(SiteGroup.class,mpjqw);
        }
        if (CollectionUtils.isEmpty(siteGroups)){
            return List.of();
        }
        return siteGroups.stream()
                .flatMap(group -> Arrays.stream(group.getApp().split(",")))
                .map(s -> s.replaceAll("[\\[\\]\"]", ""))
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }
}
