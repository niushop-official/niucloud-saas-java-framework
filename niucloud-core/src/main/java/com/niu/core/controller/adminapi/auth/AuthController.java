package com.niu.core.controller.adminapi.auth;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.EditAuthUserParam;
import com.niu.core.service.admin.auth.vo.AuthUserInfoVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 菜单管理
 */
@RestController
@RequestMapping("/adminapi/auth")
@SaCheckLogin
public class AuthController {

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    IAuthService authService;

    @Resource
    ISiteService siteService;

    @Resource
    ILoginService loginService;


    /**
     * 登录用户菜单列表接口
     *
     * @return
     */
    @GetMapping("/authmenu")
    public Result<JSONArray> authMenuList() {
        return Result.success(authService.getAuthMenuTreeList("all"));
    }

    /**
     * 获取当前登录站点信息
     *
     * @return
     */
    @GetMapping("/site")
    public Result<SiteInfoVo> siteInfo() {
        return Result.success(siteService.info(RequestUtils.siteId()));
    }

    /**
     * 获取当前登录用户的信息
     *
     * @return
     */
    @GetMapping("/get")
    public Result<AuthUserInfoVo> getAuthUserinfo() {
        return Result.success(authService.getAuthUserInfo());
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
     * 更新用户
     *
     * @param editAuthUserParam
     * @return
     */
    @PutMapping("/edit")
    public Result<Object> edit(@Validated @RequestBody EditAuthUserParam editAuthUserParam){
        authService.editAuth(editAuthUserParam);
        return Result.success();
    }
    /**
     * 退出登录
     *
     * @return
     */
    @PutMapping("/logout")
    public Result<Object> logout(){
        loginService.logout();
        return Result.success();
    }
}
