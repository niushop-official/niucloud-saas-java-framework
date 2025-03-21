package com.niu.core.controller.adminapi.wechat;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.wechat.IWechatConfigService;
import com.niu.core.service.admin.wechat.IWechatFansService;
import com.niu.core.service.admin.wechat.param.WechatFansParam;
import com.niu.core.service.admin.wechat.param.WechatFansSearchParam;
import com.niu.core.service.admin.wechat.vo.WechatFansInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatFansListVo;
import com.niu.core.service.admin.wechat.vo.WechatStaticInfoVo;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 微信粉丝列控制器
 */

@RestController
@RequestMapping("adminapi/wechat")
@SaCheckLogin
public class ConfigController {

    @Resource
    IWechatConfigService wechatConfigService;

    /**
     * 获取微信配置
     */
    @GetMapping("/config")
    public Result<WechatConfigVo> get() {
        return Result.success(wechatConfigService.getWechatConfig());
    }

    /**
     * 设置微信配置
     * @return Result<Object>
     */
    @PutMapping("/config")
    public Result<Object> set(@Validated @RequestBody WechatConfigParam wechatConfigParam) {
        wechatConfigService.setWechatConfig(wechatConfigParam);
        return Result.success();
    }

    /**
     * 微信设置的静态信息
     * @return
     */
    @GetMapping("/static")
    public Result<WechatStaticInfoVo> staticInfo() {
        return Result.success(wechatConfigService.staticInfo());
    }
}
