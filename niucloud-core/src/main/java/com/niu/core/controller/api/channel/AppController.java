package com.niu.core.controller.api.channel;

import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.system.CaptchaUtils;
import com.niu.core.enums.member.MemberLoginTypeEnum;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.api.channel.IAppService;
import com.niu.core.service.api.channel.param.GetNewVersionParam;
import com.niu.core.service.api.channel.vo.NewVersionVo;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.login.param.AccountLoginParam;
import com.niu.core.service.api.login.param.MobileLoginParam;
import com.niu.core.service.api.login.param.ResetPasswordParam;
import com.niu.core.service.api.login.param.SendMobileCodeParam;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AppController {

    @Resource
    private IAppService appService;

    @PostMapping("/wxapp/login")
    public Result<LoginVo> wechatLogin(@Validated @RequestBody WechatAuthParam param){
        return Result.success(appService.wechatLogin(param));
    }

    /**
     * 获取新的版本
     * @param param
     * @return
     */
    @GetMapping("/app/newversion")
    public Result<NewVersionVo> getNewVersion(GetNewVersionParam param){
        return Result.success(appService.getNewVersion(param));
    }
}