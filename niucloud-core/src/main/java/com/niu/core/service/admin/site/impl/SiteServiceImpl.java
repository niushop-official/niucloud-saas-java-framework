package com.niu.core.service.admin.site.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.niu.core.enums.site.AddonChildMenuEnum;
import com.niu.core.enums.site.ShowMarketingEnum;
import com.niu.core.enums.site.SiteInitEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.event.site.SiteEditAfterEvent;
import com.niu.core.event.sys.ShowCustomerEventDefiner;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.generator.IGenerateService;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.*;
import com.niu.core.service.admin.site.vo.*;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.SysUserParam;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;

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

    @Resource
    private IDiyService diyService;

    @Resource
    @Lazy
    private IAuthService authService;

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

        MPJQueryWrapper<Site> queryWrapper = new MPJQueryWrapper();
        queryWrapper.setAlias("se")
                .selectAll(Site.class)
                .select("sg.group_name")
                .leftJoin("?_site_group sg on sg.group_id = se.group_id".replace("?_", GlobalConfig.tablePrefix));


        //查询条件判断组装
        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.eq("se.site_id", searchParam.getKeywords()).or().like("se.site_name", searchParam.getKeywords());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getApp())) {
            queryWrapper.and(wrapper -> wrapper
                    .like("sg.addon", searchParam.getApp())
                    .or()
                    .like("sg.app", searchParam.getApp())
            );
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("se.status", searchParam.getStatus());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getGroupId())) {
            queryWrapper.eq("se.group_id", searchParam.getGroupId());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getSiteDomain())) {
            queryWrapper.like("se.site_domain", searchParam.getSiteDomain());
        }
        queryWrapper.ne("se.app_type", "admin");
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {

            String[] createTime = searchParam.getCreateTime();
            long startTime = (createTime[0] == null) ? 0L : DateUtils.StringToTimestamp(createTime[0]);
            long endTime = (createTime[1] == null) ? 0L : DateUtils.StringToTimestamp(createTime[1]);
            if (startTime > 0L && endTime > 0L) {
                queryWrapper.between("se.create_time", startTime, endTime);
            } else if (startTime > 0L && endTime == 0L) {
                queryWrapper.ge("se.create_time", startTime);
            } else if (startTime == 0L && endTime > 0L) {
                queryWrapper.le("se.create_time", startTime);
            }
        }

        if (ObjectUtil.isNotEmpty(searchParam.getExpireTime())) {

            String[] expireTime = searchParam.getExpireTime();
            long startTime = (expireTime[0] == null) ? 0 : DateUtils.StringToTimestamp(expireTime[0]);
            long endTime = (expireTime[1] == null) ? 0 : DateUtils.StringToTimestamp(expireTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("se.expire_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("se.expire_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le("se.expire_time", startTime);
            }
        }

        queryWrapper.orderByDesc("se.create_time");

        IPage<SiteListVo> iPage = siteMapper.selectJoinPage(new Page<>(page, limit), SiteListVo.class, queryWrapper);

        List<SiteListVo> list = new LinkedList<>();
        for (SiteListVo item : iPage.getRecords()) {
            SiteListVo vo = new SiteListVo();
            BeanUtils.copyProperties(item, vo);
            MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
            userRoleMPJQueryWrapper.select("nsu.uid, nsu.username, nsu.head_img, nsu.real_name, nsu.last_ip, nsu.last_time, nsu.create_time, nsu.login_count")
                    .setAlias("nsur")
                    .leftJoin("?_sys_user nsu ON nsur.uid = nsu.uid".replace("?_", GlobalConfig.tablePrefix));
            userRoleMPJQueryWrapper.eq("nsur.is_admin", 1);
            userRoleMPJQueryWrapper.eq("nsur.site_id", item.getSiteId());
            vo.setAdmin(sysUserRoleMapper.selectJoinOne(SiteAdminVo.class, userRoleMPJQueryWrapper));
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
        model.setExpireTime(DateUtils.StringToTimestamp(addParam.getExpireTime()));
        model.setStatus(model.getExpireTime() > DateUtils.currTime() ? SiteStatusEnum.ON.getCode() : SiteStatusEnum.EXPIRE.getCode());
        model.setIcon("static/resource/images/site/site_icon.jpg");
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
            addParam.setUid(sysUserService.addSiteUser(siteUserParam, siteId));
        } else {
            SysUserRoleParam sysUserRoleParam = new SysUserRoleParam();
            sysUserRoleParam.setUid(addParam.getUid());
            sysUserRoleParam.setRoleIds(new JsonArray().toString());
            sysUserRoleParam.setIsAdmin(1);
            sysUserRoleParam.setSiteId(siteId);
            userRoleService.add(sysUserRoleParam);
        }

        cached.remove("user_role_list_" + addParam.getUid());

        SiteAddAfterEvent event = new SiteAddAfterEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setName("SiteAddAfterEvent");
        event.setSite(model);
        event.setSiteGroup(siteGroup);
        EventAndSubscribeOfPublisher.publishAll(event);

        Map<String, Object> param = new HashMap<>();
        param.put("site_id", siteId);
        param.put("main_app", siteGroup.getApp());
        param.put("tag", "add");
        diyService.loadDiyData(param);
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

        if (ObjectUtil.isNotEmpty(model.getSiteDomain()) && model.getSiteDomain().equals(editParam.getSiteDomain())) {
            cached.remove("site_domain_" + model.getSiteDomain());
        }

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

        model.setApp(siteGroup.getApp());
        model.setInitalledAddon(JSONUtil.toJsonStr(initallJsonArray));
        model.setSiteDomain(editParam.getSiteDomain());
        model.setExpireTime(DateUtils.StringToTimestamp(editParam.getExpireTime()));
        model.setStatus(model.getExpireTime() > DateUtils.currTime() ? SiteStatusEnum.ON.getCode() : SiteStatusEnum.EXPIRE.getCode());
        siteMapper.updateById(model);

        if (ObjectUtil.isNull(editParam.getUid()) || editParam.getUid() == 0) {
            //添加用户
            SysUserParam sysUserParam = new SysUserParam();
            sysUserParam.setUsername(editParam.getUsername());
            sysUserParam.setHeadImg("");
            sysUserParam.setStatus(1);
            sysUserParam.setRealName(editParam.getRealName());
            sysUserParam.setPassword(editParam.getPassword());
            editParam.setUid(sysUserService.add(sysUserParam));
        }
        SysUserRoleParam sysUserRoleParam = new SysUserRoleParam();
        sysUserRoleParam.setUid(editParam.getUid());
        sysUserRoleParam.setRoleIds(new JsonArray().toString());
        sysUserRoleParam.setIsAdmin(1);
        sysUserRoleParam.setSiteId(id);
        userRoleService.editAdmin(sysUserRoleParam);

        cached.remove("user_role_list_" + editParam.getUid());

        // 更新微页面数据
        Map<String, Object> param = new HashMap<>();
        param.put("site_id", id);
        param.put("main_app", siteGroup.getApp());
        param.put("tag", "update");
        diyService.loadDiyData(param);

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
        Boolean result = coreSiteService.siteInitBySiteId(siteId, tables);

        Map<String, Object> param = new HashMap<>();
        param.put("site_id", siteId);
        param.put("main_app", siteInfo.getApp());
        param.put("tag", "add");
        diyService.loadDiyData(param);

        coreSiteService.clearSiteCache(siteId);

        return result;
    }

    @Override
    public SpecialMenuListVo getSpecialMenuList() {
        JSONArray authMenuList = authService.getAuthMenuTreeList(1, "all");

        // 将菜单列表转换为Map，便于通过menu_key查找
        Map<String, JSONObject> authMenuMap = new HashMap<>();
        for (int i = 0; i < authMenuList.size(); i++) {
            JSONObject menu = authMenuList.getJSONObject(i);
            authMenuMap.put(menu.get("menu_key").toString(), menu);
        }

        // 获取"addon"菜单
        JSONObject addonMenu = authMenuMap.get("addon");
        if (addonMenu == null) {
            return new SpecialMenuListVo("addon", new ArrayList<>());
        }

        Map<String, Object> showList = showCustomer(false);
        List<AddonChildMenuEnum.MenuConfig> addonChildMenus = AddonChildMenuEnum.getAll();

        List<SpecialMenuListVo.MenuVo> menuList = new ArrayList<>();

        // 遍历插件子菜单，构建菜单列表
        for (AddonChildMenuEnum.MenuConfig item : addonChildMenus) {
            String menuKey = item.getKey();
            List<String> menuKeyList = new ArrayList<>();
            if (showList.containsKey(menuKey) && showList.get(menuKey) instanceof Map) {
                Map<String, Object> menuItem = (Map<String, Object>) showList.get(menuKey);
                if (menuItem.containsKey("list") && menuItem.get("list") instanceof List) {
                    List<Map<String, Object>> listItems = (List<Map<String, Object>>) menuItem.get("list");
                    for (Map<String, Object> listItem : listItems) {
                        if (listItem.containsKey("key")) {
                            menuKeyList.add(listItem.get("key").toString());
                        }
                    }
                }
            }
            SpecialMenuListVo.MenuVo tempMenu = new SpecialMenuListVo.MenuVo();
            tempMenu.setAppType("site");
            tempMenu.setMenuName(item.getName());
            tempMenu.setMenuKey(item.getKey());
            tempMenu.setMenuShortName(item.getShortName());
            tempMenu.setParentKey("addon");
            tempMenu.setMenuType("0");
            tempMenu.setIcon("iconfont iconzhuangxiu3");
            tempMenu.setApiUrl("");
            tempMenu.setRouterPath("app/index");
            tempMenu.setViewPath("app/index");
            tempMenu.setMethods("get");
            tempMenu.setSort(item.getSort());
            tempMenu.setStatus("1");
            tempMenu.setIsShow(1);

            List<SpecialMenuListVo.MenuVo> children = new ArrayList<>();
            JSONArray authChildren = addonMenu.getJSONArray("children");
            if (authChildren != null) {
                for (int j = 0; j < authChildren.size(); j++) {
                    JSONObject child = authChildren.getJSONObject(j);
                    if (menuKeyList.contains(child.get("menu_key").toString())) {
                        // 转换为MenuVo对象
                        SpecialMenuListVo.MenuVo childMenu = convertToMenuVo(child);
                        children.add(childMenu);
                    }
                }
            }

            tempMenu.setChildren(children);
            menuList.add(tempMenu);
        }
        SpecialMenuListVo result = new SpecialMenuListVo();
        result.setParentKey("addon");
        result.setList(menuList);

        return result;
    }

    @Override
    public Map<String, Object> showCustomer(boolean isSort) {
        // 创建并设置事件
        ShowCustomerEventDefiner.ShowCustomerEvent event = new ShowCustomerEventDefiner.ShowCustomerEvent();
        event.setName("ShowCustomer");
        event.setSiteId(RequestUtils.siteId());

        // 发布事件并获取所有监听器的返回结果
        List<ShowCustomerEventDefiner.ShowCustomerEventResult> eventResults =
                EventAndSubscribeOfPublisher.publishAndCallbackBySite(event);

        // 转换事件结果为统一格式
        List<Map<String, List<ShowCustomerEventDefiner.MenuItem>>> showList = new ArrayList<>();
        for (ShowCustomerEventDefiner.ShowCustomerEventResult result : eventResults) {
            Map<String, List<ShowCustomerEventDefiner.MenuItem>> menuMap = result.getData();
            showList.add(menuMap);
        }

        // 获取插件类型列表
        List<AddonChildMenuEnum.MenuConfig> addonTypeList = AddonChildMenuEnum.getAll();
        if (addonTypeList == null) {
            addonTypeList = new ArrayList<>();
        }

        // 初始化返回结果并合并事件数据
        Map<String, Object> result = new HashMap<>();
        for (Map<String, List<ShowCustomerEventDefiner.MenuItem>> eventItem : showList) {
            for (AddonChildMenuEnum.MenuConfig type : addonTypeList) {
                String key = type.getKey();
                // 初始化类型条目
                if (!result.containsKey(key)) {
                    Map<String, Object> typeItem = new HashMap<>();
                    typeItem.put("title", type.getName());
                    typeItem.put("sort", type.getSort());
                    typeItem.put("list", new ArrayList<Map<String, Object>>());
                    result.put(key, typeItem);
                }

                // 合并列表数据
                Map<String, Object> typeItem = (Map<String, Object>) result.get(key);
                List<Map<String, Object>> typeList = (List<Map<String, Object>>) typeItem.get("list");

                // 将MenuItem转换为Map并添加到列表
                List<ShowCustomerEventDefiner.MenuItem> menuItems = eventItem.getOrDefault(key, new ArrayList<>());
                for (ShowCustomerEventDefiner.MenuItem item : menuItems) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("title", item.getTitle());
                    itemMap.put("desc", item.getDesc());
                    itemMap.put("icon", item.getIcon());
                    itemMap.put("key", item.getKey());
                    itemMap.put("url", item.getUrl());
                    typeList.add(itemMap);
                }
            }
        }

        // 收集已存在的key，用于处理未实现事件的插件
        Set<String> existingKeys = new HashSet<>();
        for (Object value : result.values()) {
            Map<String, Object> typeItem = (Map<String, Object>) value;
            List<Map<String, Object>> list = (List<Map<String, Object>>) typeItem.get("list");
            for (Map<String, Object> item : list) {
                if (item.containsKey("key")) {
                    existingKeys.add(item.get("key").toString());
                }
            }
        }

        // 处理未实现事件的插件，添加到addon_tool
        List<Addon> siteAddons = getSiteAddons();
        if (siteAddons != null && !siteAddons.isEmpty()) {
            // 查询菜单表获取路由路径
            List<String> addonKeys = siteAddons.stream()
                    .map(Addon::getKey)
                    .collect(Collectors.toList());


            List<SysMenu> menuList = sysMenuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                    .select(SysMenu::getAddon, SysMenu::getRouterPath)
                    .in(!CollectionUtils.isEmpty(addonKeys),SysMenu::getAddon, addonKeys)
                    .notIn(!CollectionUtils.isEmpty(existingKeys), SysMenu::getAddon, existingKeys)
                    .eq(SysMenu::getMenuType, 1)
                    .eq(SysMenu::getIsShow, 1)
                    .orderByAsc(SysMenu::getId)
                    .groupBy(SysMenu::getAddon));
            Map<String, String> addonUrls = menuList.stream()
                    .collect(Collectors.toMap(
                            SysMenu::getAddon,
                            SysMenu::getRouterPath,
                            // 处理重复key的情况，保留第一个
                            (existingValue, newValue) -> existingValue
                    ));

            // 初始化addon_tool条目
            if (!result.containsKey("addon_tool")) {
                Map<String, Object> toolItem = new HashMap<>();
                toolItem.put("title", "附加工具");
                toolItem.put("sort", 0);
                toolItem.put("list", new ArrayList<Map<String, Object>>());
                result.put("addon_tool", toolItem);
            }

            Map<String, Object> toolItem = (Map<String, Object>) result.get("addon_tool");
            List<Map<String, Object>> toolList = (List<Map<String, Object>>) toolItem.get("list");

            // 添加未处理的插件
            for (Addon addon : siteAddons) {
                String key = addon.getKey();
                if (existingKeys.contains(key)) {
                    continue;
                }

                Map<String, Object> item = new HashMap<>();
                item.put("title", addon.getTitle());
                item.put("desc", addon.getDesc());
                item.put("icon", addon.getIcon());
                item.put("key", key);

                String url = addonUrls.getOrDefault(key, "");
                item.put("url", url.startsWith("/") ? url : "/" + url);

                toolList.add(item);
            }
        }

        // 排序处理
        if (isSort) {
            List<Map.Entry<String, Object>> entryList = new ArrayList<>(result.entrySet());
            entryList.sort((a, b) -> {
                Map<String, Object> mapA = (Map<String, Object>) a.getValue();
                Map<String, Object> mapB = (Map<String, Object>) b.getValue();

                int sortA = mapA.containsKey("sort") ? Convert.toInt(mapA.get("sort"), 0) : 0;
                int sortB = mapB.containsKey("sort") ? Convert.toInt(mapB.get("sort"), 0) : 0;

                return Integer.compare(sortB, sortA);  // 降序排序
            });

            // 重建排序后的map
            Map<String, Object> sortedResult = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : entryList) {
                sortedResult.put(entry.getKey(), entry.getValue());
            }
            result = sortedResult;
        }

        return result;
    }

    private SpecialMenuListVo.MenuVo convertToMenuVo(JSONObject json) {
        SpecialMenuListVo.MenuVo menuVo = JSONUtil.toBean(json, SpecialMenuListVo.MenuVo.class);

        JSONArray children = json.getJSONArray("children");
        if (children != null) {
            List<SpecialMenuListVo.MenuVo> childList = new ArrayList<>();
            for (int i = 0; i < children.size(); i++) {
                childList.add(convertToMenuVo(children.getJSONObject(i)));
            }
            menuVo.setChildren(childList);
        }

        return menuVo;
    }

}
