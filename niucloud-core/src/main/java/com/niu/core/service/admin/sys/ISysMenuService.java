package com.niu.core.service.admin.sys;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.service.admin.sys.param.SysMenuParam;
import com.niu.core.service.admin.sys.param.SysMenuSearchParam;
import com.niu.core.service.admin.sys.vo.SysMenuInfoVo;
import com.niu.core.service.admin.sys.vo.SysMenuListVo;
import com.niu.core.common.domain.PageParam;

import java.util.List;
import java.util.Map;

/**
 * 菜单服务接口类
 */
public interface ISysMenuService {

    /**
     * 菜单详情
     *
     * @param id 主键ID
     * @return SysMenuInfoVo
     */
    SysMenuInfoVo info(Integer id);

    /**
     * 菜单详情
     *
     * @param appType
     * @param menuKey
     * @return
     */
    SysMenuInfoVo get(String appType, String menuKey);

    /**
     * 菜单添加
     *
     * @param addParam 参数
     */
    void add(SysMenuParam addParam);

    /**
     * 编辑菜单
     *
     * @param appType
     * @param menuKey
     * @param editParam
     */
    void edit(String appType, String menuKey, SysMenuParam editParam);

    /**
     * 删除菜单
     *
     * @param appType
     * @param menuKey
     */
    void del(String appType, String menuKey);

    /**
     * 菜单树
     *
     * @return
     */
    JSONArray menuTree();

    /**
     * 获取所有接口菜单权限
     *
     * @param appType
     * @param status
     * @return
     */
    Map<String, List<String>> getAllApiList(String appType, Integer status);

    /**
     * 获取系统菜单(站点权限api)
     *
     * @param appType
     * @param addons
     * @return
     */
    Map<String, List<String>> getApiListBySystem(String appType, List<String> addons);

    /**
     * 通过菜单menu_key 获取接口数据
     *
     * @param appType
     * @param menuKeys
     * @return
     */
    Map<String, List<String>> getApiListByMenuKeys(String appType, List<String> menuKeys);

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
    List<SysMenu> getMenuListByCondition(String appType, Integer siteId, Integer status, Integer isButton, List<String> menuKeys, String addon);

    List<SysMenu> getMenuListByKeys(String appType, Integer siteId, Integer isTree, Integer isButton, List<String> menuKeys, String addon);

    /**
     * 获取所有接口菜单
     *
     * @param appType
     * @param status
     * @return
     */
    JSONArray getAllMenuList(String appType, Integer status);

    List<SysMenu> getAllMenuList(String appType, String status, Integer isTree, Integer isButton);

    /**
     * 查询菜单
     *
     * @param menuKey
     * @param appType
     * @return
     */
    SysMenu find(String menuKey, String appType);

    JSONArray getMenuByTypeDir(String addon);

    /**
     *
     * @param appKey
     * @param status
     * @param isTree
     * @param isButton
     * @return
     */
    JSONArray getAddonMenu(String appKey, String status, Integer isTree, Integer isButton);
}
