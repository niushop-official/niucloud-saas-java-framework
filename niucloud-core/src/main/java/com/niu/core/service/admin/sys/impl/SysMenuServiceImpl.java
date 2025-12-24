package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.BaseException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.common.utils.language.LanguageUtils;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.enums.sys.MenuSourceEnum;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.param.SysMenuParam;
import com.niu.core.service.admin.sys.vo.SysMenuInfoVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import org.springframework.util.DigestUtils;

import java.util.*;

/**
 * 菜单实现类
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService {

    /** 菜单tag */
    public static String cacheTagName = CacheTagEnum.MENU_CACHE.getTagName();

    /** 是否使用缓存 */
    static final boolean useCache = true;

    @Resource
    SysMenuMapper sysMenuMapper;

    @Resource
    ICoreSiteService coreSiteService;

    @Resource
    private Cached cached;

    /**
     * 菜单详情
     *
     * @param id 主键参数
     * @return SysMenu
     */
    @Override
    public SysMenuInfoVo info(Integer id) {
        SysMenu model = sysMenuMapper.selectOne(
                new QueryWrapper<SysMenu>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SysMenuInfoVo vo = new SysMenuInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 菜单详情
     *
     * @param appType
     * @param menuKey
     * @return
     */
    @Override
    public SysMenuInfoVo get(String appType, String menuKey) {
        QueryWrapper<SysMenu> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("app_type", appType);
        queryWrapper.eq("menu_key", menuKey);
        SysMenu sysMenu=sysMenuMapper.selectOne(queryWrapper);
        Assert.notNull(sysMenu, "菜单数据不存在");
        SysMenuInfoVo sysMenuInfoVo=new SysMenuInfoVo();
        BeanUtil.copyProperties(sysMenu, sysMenuInfoVo);
        return sysMenuInfoVo;
    }

    /**
     * 菜单新增
     *
     * @param addParam 参数
     */
    @Override
    @Transactional
    public void add(SysMenuParam addParam) {
        SysMenu sysMenu=this.find(addParam.getMenuKey(), addParam.getAppType());
        if(ObjectUtil.isNotNull(sysMenu)){
            throw new AdminException("validate_menu.exit_menu_key");
        }

        SysMenu model = new SysMenu();
        BeanUtil.copyProperties(addParam, model);
        model.setCreateTime(DateUtils.currTime());
        model.setSource(MenuSourceEnum.CREATE.getCode());
        sysMenuMapper.insert(model);
        /** 清理缓存 */
        cached.tag(cacheTagName).clear();
    }

    /**
     * 编辑菜单
     *
     * @param appType
     * @param menuKey
     * @param editParam
     */
    @Override
    public void edit(String appType, String menuKey, SysMenuParam editParam) {
        SysMenu model = sysMenuMapper.selectOne(
                new QueryWrapper<SysMenu>()
                        .eq("app_type", appType)
                        .eq("menu_key", menuKey)
                        .last("limit 1"));
        Assert.notNull(model, "数据不存在!");
        BeanUtil.copyProperties(editParam, model);
        sysMenuMapper.updateById(model);
        /** 清理缓存 */
        cached.tag(cacheTagName).clear();
    }

    /**
     * 删除菜单
     *
     * @param appType
     * @param menuKey
     */
    @Override
    public void del(String appType, String menuKey) {
        Long keyCount=sysMenuMapper.selectCount(new QueryWrapper<SysMenu>().eq("parent_key", menuKey).eq("app_type", appType));
        if(keyCount>0){
            throw new AdminException("MENU_NOT_ALLOW_DELETE");
        }
        sysMenuMapper.delete(new QueryWrapper<SysMenu>().eq("app_type", appType).eq("menu_key", menuKey));
        /** 清理缓存 */
        cached.tag(cacheTagName).clear();
    }

    @Override
    public JSONArray menuTree() {

        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("menuTree"), uniqueKey ->{

            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_type", "site");
            queryWrapper.orderByDesc(Arrays.asList("sort", "id"));
            List<SysMenu> sysMenuList = sysMenuMapper.selectList(queryWrapper);
            JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(sysMenuList));
            return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
        });
    }

    @Override
    public Map<String, List<String>> getAllApiList(String app_type, Integer status) {

        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("getAllApiList", app_type, status), uniqueKey ->{
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.ne("api_url", "");
            queryWrapper.eq("app_type", app_type);
            if (status != 100) {
                queryWrapper.eq("status", status);
            }
            List<SysMenu> list = sysMenuMapper.selectList(queryWrapper);
            Map<String, List<String>> apiMap = new HashMap<>();
            for (SysMenu sysMenu : list) {
                if (!apiMap.containsKey(sysMenu.getMethods())) {
                    apiMap.put(sysMenu.getMethods(), new ArrayList<String>());
                }
                apiMap.get(sysMenu.getMethods()).add(sysMenu.getApiUrl());
            }
            return apiMap;
        });
    }

    /**
     * 获取系统菜单(站点权限api)
     *
     * @param appType
     * @param addons
     * @return
     */
    @Override
    public Map<String, List<String>> getApiListBySystem(String appType, List<String> addons) {

        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("getApiListBySystem", appType, addons), uniqueKey ->{
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (addons.size() > 0) {
                queryWrapper.in("addon", addons);
            }
            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }
            queryWrapper.orderByDesc("sort");
            List<SysMenu> list = sysMenuMapper.selectList(queryWrapper);
            Map<String, List<String>> apiMap = new HashMap<>();
            for (SysMenu sysMenu : list) {
                if (!apiMap.containsKey(sysMenu.getMethods())) {
                    apiMap.put(sysMenu.getMethods(), new ArrayList<String>());
                }
                apiMap.get(sysMenu.getMethods()).add(sysMenu.getApiUrl());
            }
            return apiMap;
        });
    }

    /**
     * 通过菜单menu_key 获取接口数据
     *
     * @param appType
     * @param menuKeys
     * @return
     */
    @Override
    public Map<String, List<String>> getApiListByMenuKeys(String appType, List<String> menuKeys) {
        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("getApiListByMenuKeys", appType, menuKeys), uniqueKey ->{
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (menuKeys.size() > 0) {
                queryWrapper.in("menu_key", menuKeys);
            } else {
                queryWrapper.in("menu_key", " ");
            }

            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }
            queryWrapper.orderByDesc("sort");
            List<SysMenu> list = sysMenuMapper.selectList(queryWrapper);
            Map<String, List<String>> apiMap = new HashMap<>();
            for (SysMenu sysMenu : list) {
                if (!apiMap.containsKey(sysMenu.getMethods())) {
                    apiMap.put(sysMenu.getMethods(), new ArrayList<String>());
                }
                apiMap.get(sysMenu.getMethods()).add(sysMenu.getApiUrl());
            }
            return apiMap;
        });
    }

    /**
     * 通过条件查询菜单
     *
     * @param appType
     * @param siteId
     * @param status
     * @param isButton
     * @param menuKeys
     * @param addon
     * @return
     */
    @Override
    public List<SysMenu> getMenuListByCondition(String appType, Integer siteId, Integer status, Integer isButton, List<String> menuKeys, String addon) {
        return cached.remember(useCache, cacheTagName, Arrays.asList("getMenuListByCondition", appType, siteId, status, isButton, menuKeys, addon), uniqueKey -> {
            List<String> addonList = coreSiteService.getAddonKeysBySiteId(siteId);
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }
            if (addonList.size() > 0) {
                addonList.add("");
                queryWrapper.in("addon", addonList);
            } else {
                queryWrapper.in("addon", "");
            }
            if (status != 100) {
                queryWrapper.eq("status", status);
            }
            if (menuKeys.size() > 0) {
                queryWrapper.in("menu_key", menuKeys);
            }
            if (isButton == 0) {
                queryWrapper.ne("menu_type", 2);
            }

            //排除菜单 后期处理

            List<SysMenu> menuList = sysMenuMapper.selectList(queryWrapper);
            return menuList;
        });
    }

    @Override
    public List<SysMenu> getMenuListByKeys(String appType, Integer siteId, Integer isTree, Integer isButton, List<String> menuKeys, String addon) {
        String menuKeysVal = String.join("_", menuKeys);
        String menuKeysValMD5 = SecureUtil.md5(menuKeysVal);
        String menuCatch = "menu" + menuKeysValMD5 + isTree.toString() + addon + siteId;
        List<SysMenu> outterMenuList = cached.cache(useCache, cacheTagName, menuCatch, uniqueKey -> {
            //通过站点id获取支持的应用插件
            List<String> addonList = coreSiteService.getAddonKeysBySiteId(siteId);

            //组装查询条件
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (menuKeys.size() > 0) {
                queryWrapper.in("menu_key", menuKeys);
            }
            List<List<String>> deleteMenuAddon = new LinkedList();
            if(!addon.equals("all")){
                queryWrapper.eq("addon",addon);

                JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
                JSONObject jsonObject = jsonModuleLoader.getResultElement(addon,  "menu/site.json");
                JSONArray delJsonInfo = ObjectUtil.defaultIfNull(jsonObject.getJSONArray("delete"), new JSONArray());
                if (ObjectUtil.isNotEmpty(delJsonInfo)) {
                    deleteMenuAddon.add(JSONUtil.toList(delJsonInfo, String.class));
                }
            }else{
                addonList.add("");
                queryWrapper.in("addon", addonList);
                for(int i=0; i<addonList.size(); i++){
                    JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
                    JSONObject jsonObject = jsonModuleLoader.getResultElement(addonList.get(i),  "menu/site.json");
                    JSONArray delJsonInfo = ObjectUtil.defaultIfNull(jsonObject.getJSONArray("delete"), new JSONArray());
                    if (ObjectUtil.isNotEmpty(delJsonInfo)) {
                        deleteMenuAddon.add(JSONUtil.toList(delJsonInfo, String.class));
                    }
                }
            }
            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }

            //排除菜单
            if(ObjectUtil.isNotEmpty(deleteMenuAddon)){
                List<String> deleteIntersect = intersect(deleteMenuAddon);
                if (ObjectUtil.isNotEmpty(deleteIntersect)) {
                    queryWrapper.notIn("menu_key", deleteIntersect);
                }
            }
            queryWrapper.orderByDesc("sort");

            List<SysMenu> menuList = sysMenuMapper.selectList(queryWrapper);
            for (SysMenu menu : menuList) {
                String langMenuKey = "dict_menu_" + menu.getAppType() + "." + menu.getMenuKey();
                String langMenuName = LanguageUtils.get(langMenuKey);

                if (!langMenuKey.equals(langMenuName)) {
                    menu.setMenuName(langMenuName);
                }
            }
            return menuList;
        });
        return outterMenuList;
    }

    /**
     * 获取所有接口菜单
     *
     * @param appType
     * @param status
     * @return
     */
    @Override
    public JSONArray getAllMenuList(String appType, Integer status) {
        //站点id
        Integer siteId= RequestUtils.siteId();

        // 缓存Key值计算参数
        String key = "menuList_"+appType+"_"+status + siteId.toString();

        List<SysMenu> outterMenuList= cached.cache(useCache, cacheTagName, key, uniqueKey -> {
            List<SysMenu> menuList = new ArrayList<>();
            try {
                QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
                if (ObjectUtil.isNotEmpty(appType)) {
                    queryWrapper.eq("app_type", appType);
                }

                //站点相关插件
                if (!RequestUtils.defaultSiteId().equals(siteId)) {
                    List<String> addonList = coreSiteService.getAddonKeysBySiteId(siteId);

                    if (addonList.size() > 0) {
                        addonList.add("");
                        queryWrapper.in("addon", addonList);
                    }
                }

                if (status != 100) {
                    queryWrapper.eq("status", status);
                }
                //排除菜单
                menuList = sysMenuMapper.selectList(queryWrapper);
            } catch (Exception e) {
                throw new BaseException("查询菜单错误");
            }
            return menuList;
        });

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(outterMenuList));
        return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
    }

    @Override
    public List<SysMenu> getAllMenuList(String appType, String status, Integer isTree, Integer isButton) {
        //站点id
        Integer siteId= RequestUtils.siteId();

        // 缓存Key值计算参数
        String key = "site_menu_api_"+appType+"_"+status +"_"+ isTree.toString() +"_"+ isButton.toString() +"_"+ siteId.toString();

        List<SysMenu> outterMenuList= cached.cache(useCache, cacheTagName, key, uniqueKey -> {
            List<SysMenu> menuList = new ArrayList<>();
            try {
                QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
                if (ObjectUtil.isNotEmpty(appType)) {
                    queryWrapper.eq("app_type", appType);
                }

                //站点相关插件
                List<String> addonList = coreSiteService.getAddonKeysBySiteId(siteId);
                addonList.add("");
                queryWrapper.in("addon", addonList);

                //排除菜单
                List<List<String>> deleteMenuAddon = new LinkedList();
                for(int i=0; i<addonList.size(); i++){
                    JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
                    JSONObject jsonObject = jsonModuleLoader.getResultElement(addonList.get(i),  "menu/site.json");
                    JSONArray delJsonInfo = ObjectUtil.defaultIfNull(jsonObject.getJSONArray("delete"), new JSONArray());
                    if (ObjectUtil.isNotEmpty(delJsonInfo)) {
                        deleteMenuAddon.add(JSONUtil.toList(delJsonInfo, String.class));
                    }
                }
                if(ObjectUtil.isNotEmpty(deleteMenuAddon)){
                    List<String> deleteIntersect = intersect(deleteMenuAddon);
                    if (ObjectUtil.isNotEmpty(deleteIntersect)) {
                        queryWrapper.notIn("menu_key", deleteIntersect);
                    }
                }

                if (!status.equals("all")) {
                    queryWrapper.eq("status", status);
                }

                if (isButton == 0) {
                    queryWrapper.ne("menu_type", 2);
                }

                queryWrapper.orderByDesc("sort");

                menuList = sysMenuMapper.selectList(queryWrapper);
            } catch (Exception e) {
                throw new BaseException("查询菜单错误");
            }
            return menuList;
        });

        return outterMenuList;
    }

    public static <T> List<T> intersect(List<List<T>> lists) {
        if (lists == null || lists.isEmpty()) {
            return new ArrayList<>();
        }
        if (lists.size() == 1) {
            return new ArrayList<>(lists.get(0));
        }

        List<T> result = new ArrayList<>(lists.get(0));
        for (int i = 1; i < lists.size(); i++) {
            result.retainAll(lists.get(i));
            if (result.isEmpty()) break;
        }
        return result;
    }
    /**
     * 查询菜单
     *
     * @param menuKey
     * @param appType
     * @return
     */
    @Override
    public SysMenu find(String menuKey, String appType) {
        QueryWrapper<SysMenu> queryWrapper= new QueryWrapper<>();
        if(ObjectUtil.isNotNull(menuKey) && ObjectUtil.isNotEmpty(menuKey)){
            queryWrapper.eq("menu_key", menuKey);
        }
        if(ObjectUtil.isNotNull(appType) && ObjectUtil.isNotEmpty(appType)){
            queryWrapper.eq("app_type", appType);
        }
        SysMenu sysMenu=sysMenuMapper.selectOne(queryWrapper);
        return sysMenu;
    }

    @Override
    public JSONArray getMenuByTypeDir(String addon) {
        List<SysMenu> outterMenuList= cached.remember(useCache, cacheTagName, Arrays.asList("getMenuByTypeDir", addon), uniqueKey -> {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_type", "site");
            queryWrapper.eq("menu_type", "0");
            queryWrapper.eq("addon", addon.equals("system")? "": addon);
            queryWrapper.orderByDesc("sort");
            return sysMenuMapper.selectList(queryWrapper);
        });

        //暂无多语言设计
        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(outterMenuList));
        return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
    }

    @Override
    public JSONArray getAddonMenu(String appKey, String status, Integer isTree, Integer isButton) {
        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("getAddonMenu", appKey, status, isTree.toString(), isButton.toString()), uniqueKey ->{

            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_type", "site");
            queryWrapper.eq("addon", appKey);
            queryWrapper.orderByDesc(Arrays.asList("sort", "id"));
            if (!status.equals("all")) {
                queryWrapper.eq("status", status);
            }
            if (isButton == 0) {
                queryWrapper.in("menu_type", new Integer[]{0, 1});
            }
            List<SysMenu> sysMenuList = sysMenuMapper.selectList(queryWrapper);
            JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(sysMenuList));
            if (isTree == 0) {
                return jsonArray;
            }
            return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
        });
    }

    @Override
    public JSONArray getSystemMenu(String status, Integer isTree, Integer isButton) {
        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("getSystemMenu", status, isTree.toString(), isButton.toString()), uniqueKey ->{
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_type", "site");
            queryWrapper.eq("addon", "");
            queryWrapper.orderByDesc(Arrays.asList("sort", "id"));
            if (!status.equals("all")) {
                queryWrapper.eq("status", status);
            }
            if (isButton == 0) {
                queryWrapper.in("menu_type", new Integer[]{0, 1});
            }
            List<SysMenu> sysMenuList = sysMenuMapper.selectList(queryWrapper);
            JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(sysMenuList));
            if (isTree == 0) {
                return jsonArray;
            }
            return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
        });
    }
}
