package com.niu.core.service.admin.install.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.enums.sys.MenuSourceEnum;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.service.admin.install.IInstallSystemService;
import com.niu.core.service.admin.install.vo.InstallMenuVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.impl.SysMenuServiceImpl;
import com.niu.core.service.core.sys.ICoreMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InstallSystemServiceImpl implements IInstallSystemService {

    @Resource
    SysMenuMapper sysMenuMapper;

    @Resource
    ICoreMenuService coreMenuService;

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    private Cached cached;

    /**
     * 安装
     */
    @Override
    public void install() {
        this.installMenu();
    }

    /**
     * 菜单安装
     */
    @Override
    public void installMenu() {
        List<InstallMenuVo> adminMenulist = this.loadMenu(AppTypeEnum.ADMIN.getName());
        List<InstallMenuVo> siteMenulist = this.loadMenu(AppTypeEnum.SITE.getName());
        List<InstallMenuVo> menulist = new ArrayList<>();
        menulist.addAll(adminMenulist);
        menulist.addAll(siteMenulist);
        log.warn("菜单信息: " + menulist);
        sysMenuMapper.delete(new QueryWrapper<SysMenu>().eq("addon", "").eq("source", MenuSourceEnum.SYSTEM.getCode()));
        for (InstallMenuVo menuVo : menulist) {
            SysMenu sysMenu = new SysMenu();
            BeanUtil.copyProperties(menuVo, sysMenu);
            sysMenu.setCreateTime(DateUtils.currTime());
            sysMenu.setDeleteTime(0L);
            sysMenu.setAddon("");
            sysMenu.setSource(MenuSourceEnum.SYSTEM.getCode());
            sysMenuMapper.insert(sysMenu);
        }
        //插件菜单
        coreMenuService.refreshAllAddonMenu();
        //清理缓存
        cached.tag(SysMenuServiceImpl.cacheTagName).clear();
    }

    private List<InstallMenuVo> childList = new ArrayList<>();

    /**
     * 加载菜单
     *
     * @param appType
     * @return
     */
    @Override
    public List<InstallMenuVo> loadMenu(String appType) {
        JSONArray jsonArray = JsonModuleLoader.build().mergeResultSet("menu/" + appType + ".json");
        List<InstallMenuVo> installMenuVoList = jsonArray.toList(InstallMenuVo.class);
        List<InstallMenuVo> menuVoList = new ArrayList<>();
        for (InstallMenuVo installMenuVo : installMenuVoList) {
            installMenuVo.setAppType(appType);
            installMenuVo.setStatus(1);
            menuVoList.add(installMenuVo);
            //递归循环遍历
            dealChildMenu(installMenuVo, appType);
            menuVoList.addAll(childList);
            childList = new ArrayList<>();
        }
        return menuVoList;
    }

    /**
     * 递归遍历菜单
     *
     * @param installMenuVo
     * @param appType
     */
    public void dealChildMenu(InstallMenuVo installMenuVo, String appType) {
        if (ObjectUtil.isNotNull(installMenuVo.getChildren()) && installMenuVo.getChildren().size() > 0) {
            for (InstallMenuVo childMenuVo : installMenuVo.getChildren()) {
                childMenuVo.setAppType(appType);
                childMenuVo.setStatus(1);
                childMenuVo.setParentKey(installMenuVo.getMenuKey());
                childList.add(childMenuVo);
                dealChildMenu(childMenuVo, appType);
            }
        }
    }
}
