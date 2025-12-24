package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.common.MonthEnum;
import com.niu.core.enums.common.WeekEnum;
import com.niu.core.service.admin.sys.ISysConfigService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.*;
import com.niu.core.service.admin.wxoplatform.IOplatformConfigService;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("adminapi/sys")
@SaCheckLogin
public class SysConfigController {

    @Resource
    ISysConfigService sysConfigService;

    @Resource
    IOplatformConfigService oplatformConfigService;

    /**
     * 获取网站信息
     * @return
     */
    @GetMapping("/config/website")
    public Result<SysWebsiteVo> getWebSite(){
        return Result.success(sysConfigService.getWebSite());
    }

    /**
     * 设置网站信息
     * @param configParam
     * @return
     */
    @PutMapping("/config/website")
    public Result setCopyRight(@Validated @RequestBody SysWebsiteParam configParam){
        sysConfigService.setWebSite(configParam);
        return Result.success();
    }

    /**
     * 获取服务信息
     * @return
     */
    @GetMapping("/config/service")
    public Result<SysServiceVo> getService(){
        return Result.success(sysConfigService.getService());
    }

    /**
     * 获取版权信息
     * @return
     */
    @GetMapping("/config/copyright")
    public Result<SysCopyRightVo> getCopyRight(){
        return Result.success(sysConfigService.getCopyRight());
    }

    /**
     * 设置版权信息
     * @param sysCopyRightParam
     * @return
     */
    @PutMapping("/config/copyright")
    public Result setCopyRight(@Validated @RequestBody SysCopyRightParam sysCopyRightParam){
        sysConfigService.setCopyRight(sysCopyRightParam);
        return Result.success();
    }

    /**
     * 获取地图配置
     * @return
     */
    @GetMapping("/config/map")
    public Result<SysMapVo> getMap(){
        return Result.success(sysConfigService.getMap());
    }

    /**
     * 设置地图配置
     * @return
     */
    @PutMapping("/config/map")
    public Result setMap(@Validated @RequestBody SysMapParam configParam){
        sysConfigService.setMap(configParam);
        return Result.success();
    }

    /**
     * 获取开发者key
     * @return
     */
    @GetMapping("/config/developer_token")
    public Result<SysDeveloperTokenVo> getDeveloperToken(){
        return Result.success(sysConfigService.getDeveloperToken());
    }

    /**
     * 设置开发者key
     * @return
     */
    @PutMapping("/config/developer_token")
    public Result setDeveloperToken(@Validated @RequestBody SysDeveloperTokenParam configParam){
        sysConfigService.setDeveloperToken(configParam);
        return Result.success();
    }

    /**
     * 获取布局设置
     * @return
     */
    @GetMapping("/config/layout")
    public Result<JSONObject> getLayout(){
        return Result.success(sysConfigService.getLayout());
    }

    /**
     * 设置布局设置
     * @return
     */
    @PutMapping("/config/layout")
    public Result setLayout(@Validated @RequestBody JSONObject configParam){
        sysConfigService.setLayout(configParam);
        return Result.success();
    }

    /**
     * 获取主题色设置
     * @return
     */
    @GetMapping("/config/themecolor")
    public Result<JSONObject> getThemeColor(){
        return Result.success(sysConfigService.getThemeColor());
    }

    /**
     * 设置主题色设置
     * @return
     */
    @PutMapping("/config/themecolor")
    public Result setThemeColor(@Validated @RequestBody JSONObject configParam){
        sysConfigService.setThemeColor(configParam);
        return Result.success();
    }


    /**
     * 获取月份字典
     * @return
     */
    @GetMapping("/date/month")
    public Result<Map<Integer, String>> getMonth(){
        return Result.success(MonthEnum.getMap());
    }

    /**
     * 获取周字典
     * @return
     */
    @GetMapping("/date/week")
    public Result<Map<Integer, String>> getWeek(){
        return Result.success(WeekEnum.getMap());
    }

    /**
     * 获取域名配置
     * @return
     */
    @GetMapping("/url")
    public Result<SceneDomainVo> getUrl(){
        return Result.success(sysConfigService.getUrl());
    }

    /**
     * 获取微信开放平台开放平台配置信息
     *
     * @return
     */
    @GetMapping("/wxoplatform/config")
    public Result getConfig(){
        return Result.success(oplatformConfigService.getWxOplatformConfig());
    }

    /**
     * 获取所有渠道
     * @return
     */
    @GetMapping("/channel")
    public Result<Map> getChannel() {
        return Result.success(ChannelEnum.getMap());
    }

    @GetMapping("/info")
    public Result<Map> getSystemInfo() {
        Map<String, Object> info = new HashMap<String, Object>();
        Map<String, String> version = new HashMap<String, String>();
        version.put("version", GlobalConfig.version);
        info.put("version", version);
        return Result.success(info);
    }
}
