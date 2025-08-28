package com.niu.core.service.admin.site.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.google.gson.JsonArray;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.addon.AddonTypeEnum;
import com.niu.core.enums.site.ShowMarketingEnum;
import com.niu.core.enums.site.SiteInitEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.event.site.SiteEditAfterEvent;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.generator.IGenerateService;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.*;
import com.niu.core.service.admin.site.vo.*;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 站点实现
 */
@Log4j2
@Service
public class SiteServiceImpl implements ISiteService {

    /**
     * 站点tag
     */
    public static String cacheTagName = CacheTagEnum.SITE_CACHE.getTagName();

    /**
     * 是否使用缓存
     */
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

    @Resource
    private IGenerateService generateService;

    @Resource
    private Cached cached;

    @Resource
    SysMenuMapper sysMenuMapper;

    /**
     * 站点列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteListVo>
     */
    @Override
    public PageResult<SiteListVo> list(PageParam pageParam, SiteSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        LambdaQueryWrapper<Site> queryWrapper = new LambdaQueryWrapper<>();
        //查询条件判断组装
        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.like(Site::getSiteName, searchParam.getKeywords()).or().like(Site::getSiteId, searchParam.getKeywords());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq(Site::getStatus, searchParam.getStatus());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getGroupId())) {
            queryWrapper.eq(Site::getGroupId, searchParam.getGroupId());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getApp())) {
            queryWrapper.like(Site::getApp, searchParam.getApp()).or().like(Site::getInitalledAddon, searchParam.getApp());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getSiteDomain())) {
            queryWrapper.like(Site::getSiteDomain, searchParam.getSiteDomain());
        }
        queryWrapper.ne(Site::getAppType, "admin");
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {

            String[] createTime = searchParam.getCreateTime();
            long startTime = (createTime[0] == null) ? 0L : DateUtils.StringToTimestamp(createTime[0]);
            long endTime = (createTime[1] == null) ? 0L : DateUtils.StringToTimestamp(createTime[1]);
            if (startTime > 0L && endTime > 0L) {
                queryWrapper.between(Site::getCreateTime, startTime, endTime);
            } else if (startTime > 0L && endTime == 0L) {
                queryWrapper.ge(Site::getCreateTime, startTime);
            } else if (startTime == 0L && endTime > 0L) {
                queryWrapper.le(Site::getCreateTime, startTime);
            }
        }

        if (ObjectUtil.isNotEmpty(searchParam.getExpireTime())) {

            String[] expireTime = searchParam.getExpireTime();
            long startTime = (expireTime[0] == null) ? 0 : DateUtils.StringToTimestamp(expireTime[0]);
            long endTime = (expireTime[1] == null) ? 0 : DateUtils.StringToTimestamp(expireTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between(Site::getExpireTime, startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge(Site::getExpireTime, startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le(Site::getExpireTime, startTime);
            }
        }


        queryWrapper.orderByDesc(Site::getCreateTime);

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
            SiteGroup siteGroup = siteGroupMapper.selectById(item.getGroupId());
            if (ObjectUtil.isNotEmpty(siteGroup)) {
                vo.setGroupName(siteGroup.getGroupName());
            }

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal(), list);
    }

    /**
     * 站点详情
     *
     * @param id 主键
     * @return SiteInfoVo
     */
    @Override
    public SiteInfoVo info(Integer id) {
        return iCoreSiteService.getSiteCache(id);
    }

    /**
     * 站点添加
     *
     * @param addParam 参数
     */
    @Transactional
    @Override
    public void add(SiteAddParam addParam) {
        SiteGroup siteGroup = siteGroupService.info(addParam.getGroupId());
        if (ObjectUtil.isNull(siteGroup) || ObjectUtil.isEmpty(siteGroup)) {
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
        Integer siteId = model.getSiteId();
        if (ObjectUtil.isNull(addParam.getUid()) || addParam.getUid() == 0) {
            //添加用户
            SiteUserParam siteUserParam = new SiteUserParam();
            siteUserParam.setUsername(addParam.getUsername());
            siteUserParam.setHeadImg("");
            siteUserParam.setStatus(1);
            siteUserParam.setRealName(addParam.getRealName());
            siteUserParam.setPassword(addParam.getPassword());
            siteUserParam.setIsAdmin(1);
            sysUserService.addSiteUser(siteUserParam, siteId);
        } else {
            SysUserRoleParam sysUserRoleParam = new SysUserRoleParam();
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
                        .eq("site_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        if (ObjectUtil.isNotNull(editParam.getGroupId()) && ObjectUtil.isNotEmpty(editParam.getGroupId()) && !editParam.getGroupId().equals(model.getGroupId())) {
            model.setGroupId(editParam.getGroupId());
        }
        if (ObjectUtil.isNotNull(editParam.getSiteName()) && ObjectUtil.isNotEmpty(editParam.getSiteName())) {
            model.setSiteName(editParam.getSiteName());
        }

        SiteGroup oldSiteGroup = siteGroupService.info(model.getGroupId());

        JSONArray initallJsonArray = new JSONArray();
        if (ObjectUtil.isNull(model.getInitalledAddon()) || ObjectUtil.isEmpty(model.getInitalledAddon())) {
            initallJsonArray.addAll(JSONUtil.parseArray(oldSiteGroup.getApp()));
            initallJsonArray.addAll(JSONUtil.parseArray(oldSiteGroup.getAddon()));
        }

        SiteGroup siteGroup = siteGroupService.info(editParam.getGroupId());
        if (ObjectUtil.isNull(siteGroup) || ObjectUtil.isEmpty(siteGroup)) {
            throw new CommonException("SITE_GROUP_NOT_EXIST");
        }
        initallJsonArray.addAll(JSONUtil.parseArray(siteGroup.getApp()));
        initallJsonArray.addAll(JSONUtil.parseArray(siteGroup.getAddon()));

        model.setInitalledAddon(JSONUtil.toJsonStr(initallJsonArray));
        model.setSiteDomain(editParam.getSiteDomain());
        model.setExpireTime(new DateTime(editParam.getExpireTime()).getTime() / 1000);
        model.setStatus(model.getExpireTime() > DateUtils.currTime() ? SiteStatusEnum.ON.getCode() : SiteStatusEnum.EXPIRE.getCode());
        siteMapper.updateById(model);

        coreSiteService.clearSiteCache(id);

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
     *
     * @param id 主键ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void del(Integer id) {
        List<SysUserRole> siteUserRoles = null;
        int delResult = 0;

        try {
            Site model = siteMapper.selectById(id);
            Assert.notNull(model, "数据不存在!");

            //获取所有需要处理的Mapper
            List<Class<? extends BaseMapper<?>>> allModels = new ArrayList<>(generateService.getMappers("system"));
            coreSiteService.getAddonKeysBySiteId(id).forEach(addon ->
                    allModels.addAll(generateService.getMappers(addon))
            );

            Class<?> wrapperClass = Class.forName("com.baomidou.mybatisplus.core.conditions.query.QueryWrapper");
            Constructor<?> wrapperConstructor = wrapperClass.getConstructor(Class.class);
            Method eqMethod = wrapperClass.getMethod("eq", boolean.class, Object.class, Object.class);
            Method deleteMethod = BaseMapper.class.getMethod("delete", Wrapper.class);

            // 处理所有关联表
            for (Class<? extends BaseMapper<?>> mapperClass : allModels) {
                BaseMapper<?> mapper = (BaseMapper<?>) SpringContext.getBean(mapperClass);
                if (mapper == null) {
                    continue;
                }

                Class<?> entityClass = resolveEntityClass(mapperClass);
                if (entityClass == null || !hasSiteIdField(entityClass)) {
                    continue;
                }

                try {
                    // 创建QueryWrapper并执行删除
                    Object wrapper = wrapperConstructor.newInstance(entityClass);
                    eqMethod.invoke(wrapper, true, "site_id", id);
                    deleteMethod.invoke(mapper, wrapper);
                } catch (Exception e) {
                    log.error("删除表数据失败: {} | 原因: {}",
                            entityClass.getSimpleName(),
                            e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
                }
            }

            // 处理用户角色关系
            QueryWrapper<SysUserRole> userRoleWrapper = new QueryWrapper<>();
            userRoleWrapper.eq("site_id", id);
            siteUserRoles = sysUserRoleMapper.selectList(userRoleWrapper);
            delResult = sysUserRoleMapper.delete(userRoleWrapper);

            // 删除站点主表
            siteMapper.deleteById(id);
        } catch (Exception e) {
            log.error("删除站点失败: {}", e.getMessage(), e);
            throw new CommonException("删除失败: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }

        // 清理缓存
        if (delResult > 0 && siteUserRoles != null) {
            siteUserRoles.forEach(userRole -> {
                cached.remove("user_role_" + userRole.getUid() + "_" + id);
                cached.remove("user_role_list_" + userRole.getUid());
            });
        }
        cached.remove("site_cache_" + id);
    }

    /**
     * 解析Mapper对应的实体类
     */
    private Class<?> resolveEntityClass(Class<? extends BaseMapper<?>> mapperClass) {
        //从泛型参数解析
        Type[] types = mapperClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                if (BaseMapper.class.isAssignableFrom((Class<?>) pType.getRawType())) {
                    Type entityType = pType.getActualTypeArguments()[0];
                    if (entityType instanceof Class) {
                        return (Class<?>) entityType;
                    } else if (entityType instanceof ParameterizedType) {
                        return (Class<?>) ((ParameterizedType) entityType).getRawType();
                    }
                }
            }
        }

        return null;
    }

    /**
     * 检查实体类是否包含site_id字段
     */
    private boolean hasSiteIdField(Class<?> entityClass) {
        if (entityClass == null) return false;

        try {
            // 使用MyBatis-Plus的TableInfoHelper
            TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
            if (tableInfo != null) {
                return tableInfo.getFieldList().stream()
                        .anyMatch(f -> "site_id".equals(f.getColumn()));
            }
        } catch (Exception e) {
            log.error("检查字段存在性失败: {}", entityClass.getSimpleName(), e);
        }

        return false;
    }


    @Override
    public void closeSite(Integer siteId) {
        Site model = new Site();
        model.setSiteId(siteId);
        model.setStatus(SiteStatusEnum.CLOSE.getCode());
        siteMapper.updateById(model);
        coreSiteService.clearSiteCache(siteId);
    }

    @Override
    public void openSite(Integer siteId) {
        Site model = new Site();
        model.setSiteId(siteId);
        model.setStatus(SiteStatusEnum.ON.getCode());
        siteMapper.updateById(model);
        coreSiteService.clearSiteCache(siteId);
    }

    /**
     * 获取站点的菜单列表
     *
     * @param siteId 站点id
     * @param status 站点状态
     */
    @Override
    public Map<String, List<String>> getSiteApiList(Integer siteId, Integer status) {
        SiteInfoVo siteInfoVo = this.info(siteId);
        Map<String, List<String>> siteApi = new HashMap<>();
        if (ObjectUtil.isNull(siteInfoVo)) {
            return siteApi;
        }
        String appType = siteInfoVo.getAppType();
        if (appType.equals(AppTypeEnum.ADMIN.getName())) {
            return sysMenuService.getAllApiList(appType, status);
        } else {
            List<String> addonKeys = iCoreSiteService.getAddonKeysBySiteId(siteId);
            return sysMenuService.getApiListBySystem(appType, addonKeys);
        }
    }

    /**
     * 通过条件检索 站点数量
     *
     * @param siteSearchParam 搜索参数
     */
    @Override
    public Integer getSiteCountByCondition(SiteSearchParam siteSearchParam) {
        QueryWrapper<Site> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(siteSearchParam.getCreateTime())) {

            String[] createTime = siteSearchParam.getCreateTime();
            long startTime = (createTime[0] == null) ? 0 : DateUtils.StringToTimestamp(createTime[0]);
            long endTime = (createTime[1] == null) ? 0 : DateUtils.StringToTimestamp(createTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("create_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("create_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
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
            long startTime = (expireTime[0] == null) ? 0 : DateUtils.StringToTimestamp(expireTime[0]);
            long endTime = (expireTime[1] == null) ? 0 : DateUtils.StringToTimestamp(expireTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("expire_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("expire_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le("expire_time", startTime);
            }
        }
        Long siteCount = siteMapper.selectCount(queryWrapper);
        return siteCount.intValue();
    }

    /**
     * 获取站点的插件
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
     * @param site      站点信息
     * @param siteGroup 站点分组
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
        coreSiteService.clearSiteCache(site.getSiteId());
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    @Override
    public Map<String, ShowAppListVo> getShowAppTools() {
        Map<String, ShowAppListVo> map = new HashMap<>();
        ShowAppListVo addonVo = new ShowAppListVo();
        addonVo.setTitle("运营工具");
        addonVo.setList(new ArrayList<>());
        map.put("addon", addonVo);
        ShowAppListVo toolsVo = new ShowAppListVo();
        toolsVo.setTitle("系统工具");
        toolsVo.setList(new ArrayList<>());
        map.put("tool", toolsVo);
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject appConfig = jsonModuleLoader.mergeResultElement("sys/show_app.json");

        List<String> appKeys = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(appConfig)) {
            for (String key : appConfig.keySet()) {
                JSONArray jsonArray = appConfig.getJSONArray(key);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ShowAppListVo.AppInfo appInfo = new ShowAppListVo.AppInfo();
                    appInfo.setTitle(jsonObject.getStr("title"));
                    appInfo.setKey(jsonObject.getStr("key"));
                    appInfo.setDesc(jsonObject.getStr("desc"));
                    appInfo.setIcon(jsonObject.getStr("icon"));
                    appInfo.setUrl(jsonObject.getStr("url"));
                    if (key.equals("addon")) {
                        addonVo.getList().add(appInfo);
                    } else if (key.equals("tool")) {
                        toolsVo.getList().add(appInfo);
                    }
                    appKeys.add(appInfo.getKey());
                }

            }
        }

        Map<String, String> addonUrls = sysMenuMapper.selectList(new QueryWrapper<SysMenu>()
            .eq("is_show", 1)
            .eq("menu_type", 1)
            .groupBy("addon")
            .orderByAsc("id")
            .select("router_path", "addon")
        ).stream().collect(Collectors.toMap(SysMenu::getAddon, SysMenu::getRouterPath));

        List<Addon> siteAddons = this.getSiteAddons();
        for (Addon addon : siteAddons) {
            if (!appKeys.contains(addon.getKey())) {
                ShowAppListVo.AppInfo appInfo = new ShowAppListVo.AppInfo();
                appInfo.setTitle(addon.getTitle());
                appInfo.setKey(addon.getKey());
                appInfo.setDesc(addon.getDesc());
                appInfo.setIcon(addon.getIcon());
                appInfo.setUrl("/" + addonUrls.getOrDefault(addon.getKey(), ""));
                addonVo.getList().add(appInfo);
            }
        }
        return map;
    }

    @Override
    public Map<String, ShowMarketingVo> getShowMarketingTools() {
        return ShowMarketingEnum.getShowMarketingTools();
    }

    @Override
    public Boolean siteInit(Integer siteId) {
        SiteInfoVo siteInfo = info(siteId);
        if (ObjectUtil.isEmpty(siteInfo)) {
            throw new AdminException("站点不存在");
        }
        List<String> tables = SiteInitEnum.getSiteInitTables(siteId);
        return coreSiteService.siteInitBySiteId(siteId, tables);
    }


}
