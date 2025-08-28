package com.niu.core.controller.adminapi.auth;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.AuthMenuParam;
import com.niu.core.service.admin.auth.param.EditAuthUserParam;
import com.niu.core.service.admin.auth.vo.AuthUserInfoVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 认证管理
 */
@Description("认证管理")
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
    @Description("登录用户菜单列表接口")
    @GetMapping("/authmenu")
    public Result<JSONArray> authMenuList(@RequestParam(value = "addon", defaultValue = "all") String addon) {
        return Result.success(authService.getAuthMenuTreeList(1,addon));
    }

    /**
     * 获取当前登录站点信息
     *
     * @return
     */
    @Description("获取当前登录站点信息")
    @GetMapping("/site")
    public Result<SiteInfoVo> siteInfo() {
        return Result.success(siteService.info(RequestUtils.siteId()));
    }

    /**
     * 获取当前登录用户的信息
     *
     * @return
     */
    @Description("获取当前登录用户的信息")
    @GetMapping("/get")
    public Result<AuthUserInfoVo> getAuthUserinfo() {
        return Result.success(authService.getAuthUserInfo());
    }

    /**
     * 菜单树
     *
     * @return
     */
    @Description("菜单树")
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
    @Description("更新用户")
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
    @Description("退出登录")
    @PutMapping("/logout")
    public Result<Object> logout(){
        loginService.logout();
        return Result.success();
    }
}
