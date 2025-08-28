package com.niu.core.controller.adminapi.login;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.auth.IConfigService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.LoginConfigParam;
import com.niu.core.service.admin.auth.param.UserLoginParam;
import com.niu.core.service.admin.auth.vo.LoginConfigVo;
import com.niu.core.service.admin.auth.vo.LoginResultVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jdk.jfr.Description;

@Description("登录配置")
@RestController
@RequestMapping("adminapi/sys/config/")
public class ConfigController {

    @Resource
    IConfigService configService;

    /**
     * 登录配置
     *
     * @return
     */
    @Description("获取登录配置")
    @GetMapping("/login")
    public Result<LoginConfigVo> getConfig(){
        LoginConfigVo loginConfigVo=configService.getLoginConfig();
        return Result.success(loginConfigVo);
    }

    /**
     * 更新登录配置信息
     *
     * @param loginConfigParam
     * @return
     */
    @Description("更新登录配置信息")
    @PutMapping("/login")
    public Result setConfig(@Validated @RequestBody LoginConfigParam loginConfigParam){
        configService.setLoginConfig(loginConfigParam);
        return Result.success();
    }

}
