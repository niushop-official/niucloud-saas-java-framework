package com.niu.core.controller.core;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.common.domain.Result;
import com.niu.core.common.exception.AuthException;
import com.niu.core.service.core.addon.AddonInstallJavaTools;
import com.niu.core.service.core.addon.ICoreAddonInstallService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * 插件管理
 */
@RestController
@RequestMapping("/core/addon")
public class CoreAddonController {

    @Resource
    ICoreAddonInstallService iCoreAddonInstallService;

    /**
     * 本地插件列表
     *
     * @return
     */
    @GetMapping("/javaSetup")
    public Result<Object> getLocalAddonList() {
        AddonInstallJavaTools.installExec("shop");
        return Result.success();
    }

    /**
     * 本地插件列表
     *
     * @return
     */
    @GetMapping("/setup/{id}")
    public Result<Object> setup() {
        iCoreAddonInstallService.installCheck("shop");
        iCoreAddonInstallService.install("shop", "local");
        return Result.success();
    }

    /**
     * 本地插件列表
     *
     * @return
     */
    @GetMapping("/exception")
    public Result<Object> exception() {
        if (true) {
            throw new RuntimeException("运行时异常.");
        }
        return Result.success();
    }

    /**
     * 本地插件列表
     *
     * @return
     */
    @GetMapping("/auth")
    public Result<Object> baseException() {
        if (true) {
            throw new AuthException("异常: AuthException.");
        }
        return Result.success();
    }

    @GetMapping("/saCheckLogin")
    @SaCheckLogin
    public Result<?> saCheckLogin() {
        return Result.success("saCheckLogin");
    }

    @GetMapping("/saIgnore")
    @SaIgnore
    public Result<?> saIgnore() {
        return Result.success("saIgnore");
    }

}
