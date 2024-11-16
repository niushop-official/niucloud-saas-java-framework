package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.install.IInstallSystemService;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.param.SysMenuParam;
import com.niu.core.service.admin.sys.param.SysMenuSearchParam;
import com.niu.core.service.admin.sys.vo.SysMenuListVo;
import com.niu.core.service.admin.sys.vo.SysMenuInfoVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 菜单管理
 */
@RestController
@RequestMapping("/adminapi/sys/")
@SaCheckLogin
public class SysMenuController {

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    IInstallSystemService installSystemService;

    /**
     * 菜单列表
     *
     * @return Result<Object>
     */
    @GetMapping("/menu/{appType}")
    public Result<JSONArray> list(@Validated @PathVariable("appType") String appType) {
        return Result.success(sysMenuService.getAllMenuList(appType, 100));
    }

    /**
     * 菜单详情
     *
     * @param appType
     * @param menuKey
     * @return
     */
    @GetMapping("/menu/{appType}/info/{menuKey}")
    public Result<SysMenuInfoVo> info(@Validated @PathVariable("appType") String appType, @Validated @PathVariable("menuKey") String menuKey) {
        SysMenuInfoVo info = sysMenuService.get(appType, menuKey);
        return Result.success(info);
    }

    /**
     * 菜单添加
     *
     * @param addParam 参数
     * @return Result<Object>
     */
    @PostMapping("/menu")
    public Result<Object> add(@Validated @RequestBody SysMenuParam addParam) {
        sysMenuService.add(addParam);
        return Result.success();
    }

    /**
     * 菜单编辑
     *
     * @param editParam 参数
     * @return Result<Object>
     */
    @PutMapping("/menu/{appType}/{menuKey}")
    public Result<Object> edit(@Validated @PathVariable("appType") String appType, @Validated @PathVariable("menuKey") String menuKey, @Validated @RequestBody SysMenuParam editParam) {
        sysMenuService.edit( appType, menuKey, editParam);
        return Result.success();
    }

    /**
     * 删除菜单
     *
     * @param appType
     * @param menuKey
     * @return
     */
    @DeleteMapping("/menu/{appType}/{menuKey}")
    public Result<Object> del(@Validated @PathVariable("appType") String appType, @Validated @PathVariable("menuKey") String menuKey) {
        sysMenuService.del(appType, menuKey);
        return Result.success();
    }

    /**
     * 重置菜单
     * @return
     */
    @PostMapping("/menu/refresh")
    public Result<Object> refreshMenu(){
        installSystemService.install();
        return Result.success();
    }
    /**
     * 菜单树
     *
     * @return
     */
    @GetMapping("/tree")
    public Result<JSONArray> tree() {
        return Result.success(sysMenuService.menuTree());
    }

    /**
     * 获取菜单类型
     * @param addon
     * @return
     */
    @GetMapping("/menu/dir/{addon}")
    public Result<JSONArray> getMenuByTypeDir(@PathVariable("addon") String addon) {

        return Result.success(sysMenuService.getMenuByTypeDir(addon));
    }

}
