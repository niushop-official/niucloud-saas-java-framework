package com.niu.core.controller.api.weapp;

import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.weapp.IWeappService;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weapp")
public class WeappController {

    @Resource
    IWeappService weappService;

    /**
     * 通过授权code登录
     * @param param
     */
    @SaIgnore
    @PostMapping("/login")
    public Result<LoginVo> login(@Validated @RequestBody WechatAuthParam param) {
        return Result.success(weappService.loginByCode(param));
    }

    /**
     * 授权信息注册账号
     * @param param
     */
    @SaIgnore
    @PostMapping("/register")
    public Result<LoginVo> register(@Validated @RequestBody AuthRegisterParam param) {
        return Result.success(weappService.register(param));
    }

    /**
     * 公众号更新用户openid
     * @return
     */
    @SaIgnore
    @PutMapping("/update_openid")
    public Result<Object> updateOpenid(@Validated @RequestBody WechatAuthParam param) {
        weappService.updateOpenid(param);
        return Result.success();
    }

    /**
     * 获取小程序订阅消息模板id
     * @return
     */
    @SaIgnore
    @GetMapping("/subscribemsg")
    public Result<List<String>> subscribeMessage(@RequestParam String keys) {
        return Result.success(weappService.subscribeMessage(keys));
    }

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @return
     */
    @SaIgnore
    @GetMapping("/getIsTradeManaged")
    public Result<Map<String, Boolean>> getIsTradeManaged() {
        return Result.success(weappService.getIsTradeManaged());
    }

}