package com.niu.core.controller.adminapi.wxoplatform;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.auth.IConfigService;
import com.niu.core.service.admin.auth.param.LoginConfigParam;
import com.niu.core.service.admin.auth.vo.LoginConfigVo;
import com.niu.core.service.admin.site.param.SiteSearchParam;
import com.niu.core.service.admin.wxoplatform.IOplatformConfigService;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("adminapi/wxoplatform")
@SaCheckLogin
@Description("微信开放平台")
public class ConfigController {

    @Resource
    IOplatformConfigService oplatformConfigService;

    /**
     * 获取微信开放平台静态资源
     *
     * @return
     */
    @GetMapping("/static")
    @Description("获取微信开放平台静态资源")
    public Result<CoreOplatformStaticConfigVo> getStaticInfo(){
        return Result.success(oplatformConfigService.getOplatformStaticInfo());
    }

    /**
     * 获取微信开放平台开放平台配置信息
     *
     * @return
     */
    @GetMapping("/config")
    @Description("获取微信开放平台配置信息")
    public Result getConfig(){
        return Result.success(oplatformConfigService.getWxOplatformConfig());
    }

    /**
     * 更新微信开放平台配置信息
     *
     * @param oplatformConfigParam
     * @return
     */
    @PutMapping("/config")
    @Description("更新微信开放平台配置信息")
    public Result setConfig(@Validated @RequestBody OplatformConfigParam oplatformConfigParam){
        oplatformConfigService.setWxOplatformConfig(oplatformConfigParam);
        return Result.success();
    }

}
