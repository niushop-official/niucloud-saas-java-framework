package com.niu.core.controller.adminapi.niucloud;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.niucloud.ICloudBuildService;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.admin.niucloud.param.ConnectTestParam;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jdk.jfr.Description;

/**
 * 云编译控制器
 */
@Description("云编译控制器")
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
    @Description("获取编译任务")
    @GetMapping("/build")
    public Result getBuildTask(){
        return Result.success(cloudBuildService.getBuildTask("build"));
    }

    /**
     * 云编译
     * @return
     */
    @Description("云编译")
    @PostMapping("/build")
    public Result build(){
        return Result.success(cloudBuildService.build("build"));
    }

    /**
     * 获取云编译
     * @return
     */
    @Description("获取云编译日志")
    @GetMapping("/build/log")
    public Result getBuildLog(){
        return Result.success(cloudBuildService.getBuildLog("build"));
    }

    /**
     * 清除编译任务
     * @return
     */
    @Description("清除编译任务")
    @PostMapping("/build/clear")
    public Result clearBuildTask(){
        cloudBuildService.clearBuildTask();
        return Result.success();
    }

    /**
     * 编译前环境检测
     * @return
     */
    @Description("编译前环境检测")
    @GetMapping("/build/check")
    public Result buildPreCheck(){
        return Result.success(cloudBuildService.buildPreCheck());
    }

    /**
     * 获取本地地址
     */
    @Description("获取本地地址")
    @GetMapping("/build/get_local_url")
    public Result<?> getLocalCloudCompileConfig(){
        return Result.success(cloudBuildService.getLocalCloudCompileConfig());
    }

    /**
     * 设置本地地址
     */
    @Description("设置本地地址")
    @PostMapping("/build/set_local_url")
    public Result<?> setLocalCloudCompileConfig(@RequestBody ConnectTestParam param){
        cloudBuildService.setLocalCloudCompileConfig(param);
        return Result.success(1,"操作成功",1);
    }

    /**
     * 连通测试
     */
    @Description("连通测试")
    @PostMapping("/build/connect_test")
    public Result<?> connectTest(@RequestBody ConnectTestParam param){
        return Result.success(cloudBuildService.connectTest(true, param.getUrl()));
    }
}
