package com.niu.core.controller.adminapi.login;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.addon.param.AddonSearchParam;
import com.niu.core.service.admin.auth.IConfigService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.UserLoginParam;
import com.niu.core.service.admin.auth.vo.LoginConfigVo;
import com.niu.core.service.admin.auth.vo.LoginResultVo;
import com.niu.core.service.admin.sys.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("adminapi/login")
public class LoginController {


    @Resource
    ILoginService loginService;
    @Resource
    IConfigService configService;

    @GetMapping("/{appType}")
    public Result<LoginResultVo> login(@Validated UserLoginParam userLoginParam){
        LoginResultVo loginResultVo=loginService.login(userLoginParam);
        return Result.success(loginResultVo);
    }

    /**
     * 获取登录设置
     * @return
     */
    @GetMapping("config")
    public Result<LoginConfigVo> getConfig(){
        return Result.success(configService.getLoginConfig());
    }

    @RequestMapping("/tokenInfo")
    public SaResult tokenInfo() {
        return SaResult.data(StpUtil.getTokenInfo());
    }

    // 测试注销
    @RequestMapping("/logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }

}
