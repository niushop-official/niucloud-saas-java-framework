package com.niu.core.controller.api.login;

import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.system.CaptchaUtils;
import com.niu.core.enums.member.MemberLoginTypeEnum;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.login.param.AccountRegisterParam;
import com.niu.core.service.api.login.param.MobileRegisterParam;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.weapp.IWeappService;
import com.niu.core.service.api.wechat.IWechatService;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Resource
    IRegisterService registerService;

    @Resource
    IWechatService wechatService;

    @Resource
    IWeappService weappService;

    /**
     * 用户名密码注册
     * @param param
     * @return
     */
    @PostMapping("/register")
    public Result<LoginVo> account(@Validated @RequestBody AccountRegisterParam param) {
        registerService.checkLoginConfig(MemberLoginTypeEnum.USERNAME.getType());
        return Result.success(registerService.account(param));
    }

    /**
     * 手机验证码注册
     * @param param
     * @return
     */
    @PostMapping("/register/mobile")
    public Result<LoginVo> mobile(@Validated @RequestBody MobileRegisterParam param) {
        registerService.checkLoginConfig(MemberLoginTypeEnum.MOBILE.getType());
        registerService.checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode());
        CaptchaUtils.check(param.getCaptchaKey(), param.getCaptchaCode());
        return Result.success(registerService.mobile(param));
    }

    /**
     * 绑定
     * @param param
     * @return
     */
    public Result bind(AuthRegisterParam param) {
        switch (RequestUtils.channel()) {
            case "wechat":
                return Result.success(wechatService.register(param));
            case "weapp":
                return Result.success(weappService.register(param));
        }
        return Result.success();
    }
}
