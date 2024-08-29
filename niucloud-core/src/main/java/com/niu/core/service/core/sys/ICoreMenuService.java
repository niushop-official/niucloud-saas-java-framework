package com.niu.core.service.core.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;

public interface ICoreMenuService extends IService<SysMenu> {

    /**
     * 刷新插件菜单
     */
    void refreshAddonMenu(String addon);

    /**
     * 刷新插件菜单
     */
    void installAddonMenu(String addon);

    /**
     * 删除菜单
     * @param addon
     * @param isAll
     */
    void deleteMenu(String addon, Boolean isAll);

    /**
     * 刷新所有插件菜单
     *
     */
    void refreshAllAddonMenu();
}
