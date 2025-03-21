package com.niu.core.controller.api.wechat;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.wechat.IWechatService;
import com.niu.core.service.api.wechat.param.WechatAuthDataLoginParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatSyncParam;
import com.niu.core.service.api.wechat.vo.WechatCodeUrlVo;
import com.niu.core.service.api.wechat.vo.WechatScanLoginVo;
import com.niu.core.service.api.wechat.vo.WechatUserInfoVo;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wechat")
public class WechatController {

    @Resource
    IWechatService wechatService;

    /**
     * 获取跳转获取code
     * @param url
     * @param scopes
     * @return
     */
    @GetMapping("/codeurl")
    public Result<WechatCodeUrlVo> getCodeUrl(@RequestParam(name = "url") String url, @RequestParam(name = "scopes", defaultValue = "snsapi_base") String scopes) {
        return Result.success(wechatService.getCodeUrl(url, scopes));
    }

    /**
     * 获取网页授权信息
     * @param param
     * @return
     */
    @GetMapping("/user")
    public Result getWechatUser(WechatAuthParam param) {
        Map<String, String> result = new HashMap<>();
        result.put("data", JSONUtil.parseObj(wechatService.getWechatUser(param)).toString());
        return Result.success(result);
    }

    /**
     * 公众号通过授权信息登录
     * @param param
     * @return
     */
    @PostMapping("/userlogin")
    public Result<LoginVo> wechatLogin(@RequestBody WechatAuthDataLoginParam param) {
        return Result.success(wechatService.wechatLogin(param));
    }

    /**
     * 公众号通过授权code登录
     * @param param
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@Validated @RequestBody WechatAuthParam param) {
        return Result.success(wechatService.loginByCode(param));
    }

    /**
     * 授权信息注册账号
     * @param param
     */
    @PostMapping("/register")
    public Result<LoginVo> register(@Validated @RequestBody AuthRegisterParam param) {
        return Result.success(wechatService.register(param));
    }

    /**
     * 公众号通过code同步授权
     * @param param
     */
    @PostMapping("/sync")
    public Result sync(@Validated @RequestBody WechatSyncParam param) {
        wechatService.sync(param);
        return Result.success();
    }

    /**
     * 获取jssdk config
     * @param url
     * @return
     */
    @GetMapping("/jssdkconfig")
    public Result<Map<String, Object>> jssdkConfig(@RequestParam("url") String url) {
        return Result.success(wechatService.getJssdkConfig(url));
    }

    /**
     * 扫码登录
     * @return
     */
    @PostMapping("/scanlogin")
    public Result<WechatScanLoginVo> scanLogin() {
        return Result.success(wechatService.scanLogin());
    }

    /**
     * 公众号更新用户openid
     * @return
     */
    @PutMapping("/update_openid")
    public Result updateOpenid(@Validated @RequestBody WechatAuthParam param) {
        wechatService.updateOpenid(param);
        return Result.success();
    }
}
