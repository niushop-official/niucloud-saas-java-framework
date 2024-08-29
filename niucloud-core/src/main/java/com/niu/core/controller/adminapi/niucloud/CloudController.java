package com.niu.core.controller.adminapi.niucloud;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.niucloud.ICloudBuildService;
import com.niu.core.service.admin.niucloud.INiucloudService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 云编译控制器
 */
@RestController
@RequestMapping("/adminapi/niucloud")
@SaCheckLogin
public class CloudController {

    @Resource
    ICloudBuildService cloudBuildService;

    /**
     * 获取编译任务
     * @return
     */
    @GetMapping("/build")
    public Result getBuildTask(){
        return Result.success(cloudBuildService.getBuildTask("build"));
    }

    /**
     * 云编译
     * @return
     */
    @PostMapping("/build")
    public Result build(){
        return Result.success(cloudBuildService.build("build"));
    }

    /**
     * 获取云编译
     * @return
     */
    @GetMapping("/build/log")
    public Result getBuildLog(){
        return Result.success(cloudBuildService.getBuildLog("build"));
    }

    /**
     * 清除编译任务
     * @return
     */
    @PostMapping("/build/clear")
    public Result clearBuildTask(){
        cloudBuildService.clearBuildTask();
        return Result.success();
    }

    /**
     * 编译前环境检测
     * @return
     */
    @GetMapping("/build/check")
    public Result buildPreCheck(){
        return Result.success(cloudBuildService.buildPreCheck());
    }
}
