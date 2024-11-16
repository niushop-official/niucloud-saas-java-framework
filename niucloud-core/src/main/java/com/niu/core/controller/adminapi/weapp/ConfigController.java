package com.niu.core.controller.adminapi.weapp;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.weapp.IWeappConfigService;
import com.niu.core.service.admin.weapp.vo.WeappStaticInfoVo;
import com.niu.core.service.admin.wechat.IWechatConfigService;
import com.niu.core.service.admin.wechat.vo.WechatStaticInfoVo;
import com.niu.core.service.core.weapp.param.WeappConfigParam;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序配置
 */

@RestController
@RequestMapping("adminapi/weapp")
@SaCheckLogin
public class ConfigController {

    @Resource
    IWeappConfigService weappConfigService;

    /**
     * 获取微信小程序配置
     */
    @GetMapping("/config")
    public Result<WeappConfigVo> get() {
        return Result.success(weappConfigService.getWeappConfig());
    }

    /**
     * 设置微信小程序配置
     * @return Result<Object>
     */
    @PutMapping("/config")
    public Result<Object> set(@Validated @RequestBody WeappConfigParam weappConfigParam) {
        weappConfigService.setWeappConfig(weappConfigParam);
        return Result.success();
    }

    /**
     * 查询小程序是否已开通发货信息管理服务
     *
     * @return
     */
    @GetMapping("/delivery/getIsTradeManaged")
    public Result getIsTradeManaged() {
        Map<String, Object> data = new HashMap<>();
        data.put("is_trade_managed", false);
        return Result.success(data);
    }
}
