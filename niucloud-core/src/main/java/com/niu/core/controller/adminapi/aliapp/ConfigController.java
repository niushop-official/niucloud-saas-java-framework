package com.niu.core.controller.adminapi.aliapp;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.aliapp.IAliappConfigService;
import com.niu.core.service.admin.wechat.IWechatConfigService;
import com.niu.core.service.admin.wechat.vo.WechatStaticInfoVo;
import com.niu.core.service.core.aliapp.param.AliappConfigParam;
import com.niu.core.service.core.aliapp.vo.AliappConfigVo;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 微信粉丝列控制器
 */

@RestController
@RequestMapping("adminapi/aliapp")
@SaCheckLogin
public class ConfigController {

    @Resource
    IAliappConfigService aliappConfigService;

    /**
     * 获取微信配置
     */
    @GetMapping("/config")
    public Result<AliappConfigVo> get() {
        return Result.success(aliappConfigService.getAliappConfig());
    }

    /**
     * 设置微信配置
     * @return Result<Object>
     */
    @PutMapping("/config")
    public Result<Object> set(@Validated @RequestBody AliappConfigParam aliappConfigParam) {
        aliappConfigService.setAliappConfig(aliappConfigParam);
        return Result.success();
    }

    /**
     * 微信设置的静态信息
     * @return
     */
    @GetMapping("/static")
    public Result<Map<String, String>> staticInfo() {
        return Result.success(aliappConfigService.getStatic());
    }
}
