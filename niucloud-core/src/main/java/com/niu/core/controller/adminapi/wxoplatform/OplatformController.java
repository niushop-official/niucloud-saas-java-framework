package com.niu.core.controller.adminapi.wxoplatform;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.member.param.MemberSearchParam;
import com.niu.core.service.admin.member.vo.MemberListVo;
import com.niu.core.service.admin.wxoplatform.IOplatformConfigService;
import com.niu.core.service.admin.wxoplatform.IOplatformService;
import com.niu.core.service.admin.wxoplatform.param.AuthorizationParam;
import com.niu.core.service.admin.wxoplatform.param.IOplatformAuthRecordParam;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.admin.wxoplatform.vo.OplatformRecordVo;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jdk.jfr.Description;

@RestController
@RequestMapping("adminapi/wxoplatform")
@SaCheckLogin
@Description("微信开放平台")
public class OplatformController {

    @Resource
    IOplatformService oplatformService;

    /**
     * 获取授权页地址
     * @return
     */
    @GetMapping("/authorizationUrl")
    @Description("获取授权页地址")
    public Result<String> getAuthorizationUrl(){
        return Result.success("", oplatformService.createPreAuthorizationUrl());
    }

    /**
     * 授权
     *
     * @return
     */
    @GetMapping("/authorization")
    @Description("授权")
    public Result<Object> authorization(@RequestParam("auth_code") String authCode, @RequestParam("expires_in") Integer expiresIn){
        AuthorizationParam param = new AuthorizationParam();
        param.setAuthCode(authCode);
        param.setExpiresIn(expiresIn.toString());
        oplatformService.authorization(param);
        return Result.success();
    }

    @Description("授权记录")
    @GetMapping("/authorization/record")
    public Result<PageResult<OplatformRecordVo>> list(@Validated PageParam pageParam,
                                                 @Validated IOplatformAuthRecordParam searchParam) {
        PageResult<OplatformRecordVo> list = oplatformService.getAuthRecord(pageParam,searchParam);
        return Result.success(list);
    }


}
