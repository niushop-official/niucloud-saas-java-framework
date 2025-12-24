package com.niu.core.controller.api.weapp;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.weapp.IWeappService;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import com.niu.core.service.core.weapp.vo.IsTradeManagedVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
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
    @PostMapping("/login")
    public Result<LoginVo> login(@Validated @RequestBody WechatAuthParam param) {
        return Result.success(weappService.loginByCode(param));
    }

    /**
     * 授权信息注册账号
     * @param param
     */
    @PostMapping("/register")
    public Result<LoginVo> register(@Validated @RequestBody AuthRegisterParam param) {
        return Result.success(weappService.register(param));
    }

    /**
     * 公众号更新用户openid
     * @return
     */
    @SaCheckLogin
    @PutMapping("/update_openid")
    public Result<Object> updateOpenid(@Validated @RequestBody WechatAuthParam param) {
        weappService.updateOpenid(param);
        return Result.success();
    }

    /**
     * 获取小程序订阅消息模板id
     * @return
     */
    @GetMapping("/subscribemsg")
    public Result<List<String>> subscribeMessage(@RequestParam("keys") String keys) {
        return Result.success(weappService.subscribeMessage(keys));
    }

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @return
     */
    @GetMapping("/getIsTradeManaged")
    public Result<IsTradeManagedVo> getIsTradeManaged() {
        return Result.success(weappService.getIsTradeManaged());
    }

    /**
     * 通过外部交易号获取消息跳转路径
     * @param outTradeNo
     * @return
     */
    @GetMapping("/getMsgJumpPath")
    public Result<?> getMsgJumpPath(@RequestParam("out_trade_no") String outTradeNo) {
        return Result.success(weappService.getMsgJumpPath(outTradeNo));
    }
}