package com.niu.core.controller.adminapi.wxoplatform;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.wxoplatform.IOplatformConfigService;
import com.niu.core.service.admin.wxoplatform.IOplatformService;
import com.niu.core.service.admin.wxoplatform.param.AuthorizationParam;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/wxoplatform")
@SaCheckLogin
public class OplatformController {

    @Resource
    IOplatformService oplatformService;

    /**
     * 获取授权页地址
     * @return
     */
    @GetMapping("/authorizationUrl")
    public Result<String> getAuthorizationUrl(){
        return Result.success("", oplatformService.createPreAuthorizationUrl());
    }

    /**
     * 授权
     *
     * @return
     */
    @GetMapping("/authorization")
    public Result<Object> authorization(@RequestParam("auth_code") String authCode, @RequestParam("expires_in") Integer expiresIn){
        AuthorizationParam param = new AuthorizationParam();
        param.setAuthCode(authCode);
        param.setExpiresIn(expiresIn.toString());
        oplatformService.authorization(param);
        return Result.success();
    }

}
