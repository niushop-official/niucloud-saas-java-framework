package com.niu.core.service.core.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.service.admin.sys.impl.SysMenuServiceImpl;
import com.niu.core.service.core.sys.ICoreMenuService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.io.File;
import java.util.List;

@Service
public class CoreMenuServiceImpl extends ServiceImpl<SysMenuMapper,SysMenu> implements ICoreMenuService {

    @Resource
    SysMenuMapper sysMenuMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    SqlSessionFactory sqlSessionFactory;

    @Resource
    private Cached cached;

    @Override
    public void refreshAddonMenu(String addon) {
        this.deleteMenu(addon, false);

        JSONObject adminMenu = JsonModuleLoader.build().getResultElement(addon, "menu/admin.json");
        if(ObjectUtil.isNotEmpty(adminMenu)){
            if (adminMenu.getJSONArray("menu") != null) {
                List<SysMenu> adminMenuBeanList = this.menuTreeToList(TreeUtils.treeToList(adminMenu.getJSONArray("menu"), "parent_key", "", "menu_key", "children"), addon, "admin");
                super.saveBatch(adminMenuBeanList);
            }
        }
        JSONObject siteMenu = JsonModuleLoader.build().getResultElement(addon, "menu/site.json");
        if(ObjectUtil.isNotEmpty(siteMenu)){
            if (siteMenu.getJSONArray("menu") != null) {
                List<SysMenu> siteMenuBeanList = this.menuTreeToList(TreeUtils.treeToList(siteMenu.getJSONArray("menu"), "parent_key", "", "menu_key", "children"), addon, "site");
                super.saveBatch(siteMenuBeanList);
            }
        }

        //清理缓存
        cached.tag(SysMenuServiceImpl.cacheTagName).clear();
    }

    /**
     * 刷新菜单
     * @param addon
     */
    @Override
    public void installAddonMenu(String addon) {
        this.deleteMenu(addon, false);

        String menuDir = WebAppEnvs.get().webRootDownAddon + addon + "/menu/";
        File adminMenu = new File(menuDir + "admin.json");
        File siteMenu = new File(menuDir + "site.json");

        if (adminMenu.exists()) {
            String jsonString = JsonLoadUtils.loadJsonString(adminMenu);
            List<JSONObject> menuList = TreeUtils.treeToList(JSONUtil.parseObj(jsonString).getJSONArray("menu"), "parent_key", "", "menu_key", "children");
            List<SysMenu> adminMenuBeanList = this.menuTreeToList(menuList, addon, "admin");
            super.saveBatch(adminMenuBeanList);
        }
        if (siteMenu.exists()) {
            String jsonString = JsonLoadUtils.loadJsonString(siteMenu);
            List<JSONObject> menuList = TreeUtils.treeToList(JSONUtil.parseObj(jsonString).getJSONArray("menu"), "parent_key", "", "menu_key", "children");
            List<SysMenu> siteMenuBeanList = this.menuTreeToList(menuList, addon, "site");
            super.saveBatch(siteMenuBeanList);
        }

        //清理缓存
        cached.tag(SysMenuServiceImpl.cacheTagName).clear();
    }

    private List<SysMenu> menuTreeToList(List<JSONObject> menuList, String addon, String appType) {
        for (JSONObject item : menuList) {
            item.put("addon", addon);
            item.put("app_type", appType);
        }
        return JacksonUtils.toSnakeCaseList(menuList, SysMenu.class);
    }

    /**
     * 删除菜单
     * @param addon
     * @param isAll
     */
    public void deleteMenu(String addon, Boolean isAll) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<SysMenu>()
                .eq("addon", addon);
        if (!isAll) queryWrapper.eq("source", "system");

        sysMenuMapper.delete(queryWrapper);
    }

    /**
     * 刷新所有插件菜单
     */
    @Override
    public void refreshAllAddonMenu() {
        //查询所有的插件
        List<Addon> addonList=addonMapper.selectList(new QueryWrapper<Addon>());
        for (Addon addon: addonList) {
            this.refreshAddonMenu(addon.getKey());
        }
    }
}
