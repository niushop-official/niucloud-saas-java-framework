package com.niu.core.controller.api.login;

import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.system.CaptchaUtils;
import com.niu.core.enums.member.MemberLoginTypeEnum;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.api.channel.IAppService;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.login.param.*;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.weapp.IWeappService;
import com.niu.core.service.api.wechat.IWechatService;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Resource
    IRegisterService registerService;

    @Resource
    ILoginService loginService;

    @Resource
    IWechatService wechatService;

    @Resource
    IWeappService weappService;

    @Resource
    IAppService appService;

    /**
     * 账号登录
     *
     * @param param
     * @return
     */
    @GetMapping("/login")
    public Result<LoginVo> account(@Validated AccountLoginParam param) {
        registerService.checkLoginConfig(MemberLoginTypeEnum.USERNAME.getType());
        return Result.success(loginService.account(param));
    }

    /**
     * 手机验证码登录
     *
     * @param param
     * @return
     */
    @PostMapping("/login/mobile")
    public Result<LoginVo> mobile(@Validated @RequestBody MobileLoginParam param) {
        registerService.checkLoginConfig(MemberLoginTypeEnum.MOBILE.getType());
        registerService.checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode());
        return Result.success(loginService.mobile(param));
    }

    /**
     * 重置密码
     *
     * @param param
     * @return
     */
    @PostMapping("/password/reset")
    public Result<?> resetPassword(@Validated @RequestBody ResetPasswordParam param) {
        registerService.checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode());
        loginService.resetPassword(param);
        return Result.success();
    }

    /**
     * 登录注册配置
     *
     * @return
     */
    @GetMapping("/login/config")
    public Result<LoginConfigVo> getLoginConfig(@RequestParam(name = "url", required = false, defaultValue = "") String url) {
        return Result.success(loginService.getLoginConfig(url));
    }

    /**
     * 手机号发送验证码
     *
     * @return
     */
    @PostMapping("/send/mobile/{type}")
    public Result<?> sendMobileCode(@PathVariable("type") String type, @Validated @RequestBody SendMobileCodeParam param) {
        param.setType(type);
        CaptchaUtils.check(param.getCaptchaKey(), param.getCaptchaCode());
        return Result.success(loginService.sendMobileCode(param));
    }

    /**
     * 退出登录
     *
     * @return
     */
    @PutMapping("/auth/logout")
    @SaIgnore
    public Result logout() {
        return Result.success();
    }


    @PostMapping("/bind")
    public Result<LoginVo> bind(@RequestBody AuthRegisterParam param) {
        switch (RequestUtils.channel()) {
            case "pc":
            case "wechat":
                return Result.success(wechatService.register(param));
            case "weapp":
                return Result.success(weappService.register(param));
            case "app":
                return Result.success(appService.register(param));
        }
        return Result.success(new LoginVo());
    }
}