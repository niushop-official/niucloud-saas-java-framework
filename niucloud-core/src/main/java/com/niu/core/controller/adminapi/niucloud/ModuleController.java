package com.niu.core.controller.adminapi.niucloud;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.admin.niucloud.param.GetAppVersionListParam;
import com.niu.core.service.core.niucloud.param.SetAuthorizeParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jdk.jfr.Description;

/**
 * 授权管理控制器
 */
@Description("授权管理")
@RestController
@RequestMapping("/adminapi/niucloud")
@SaCheckLogin
public class ModuleController {

    @Resource
    INiucloudService niucloudService;


    /**
     * 获取框架最新版本
     * @return
     */
    @Description("获取框架最新版本")
    @GetMapping("/framework/newversion")
    public Result getFrameworkLastVersion(){
        return Result.success(niucloudService.getFrameworkLastVersion());
    }

    /**
     * 获取框架更新记录
     * @return
     */
    @Description("获取框架更新记录")
    @GetMapping("/framework/version/list")
    public Result getFrameworkVersionList(){
        return Result.success(niucloudService.getFrameworkVersionList());
    }


    /**
     * 查询授权信息
     * @return
     */
    @Description("查询授权信息")
    @GetMapping("/authinfo")
    public Result getAuthinfo(){
        return Result.success(niucloudService.getAuthinfo());
    }

    /**
     * 绑定授权
     *
     * @return
     */
    @Description("绑定授权")
    @PostMapping("/authinfo")
    public Result setAuthorize(@Validated @RequestBody SetAuthorizeParam param) {
        niucloudService.setAuthorize(param);
        return Result.success();
    }

    @GetMapping("/app_version/list")
    @Description("获取应用/插件的版本更新记录")
    public Result getAppVersionList(GetAppVersionListParam param){
        return Result.success(niucloudService.getAppVersionList(param));
    }
}
