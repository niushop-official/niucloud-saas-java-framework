package com.niu.core.controller.adminapi.wxoplatform;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.wxoplatform.IOplatformConfigService;
import com.niu.core.service.admin.wxoplatform.IWeappVersionService;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.admin.wxoplatform.vo.WxOplatfromWeappVersionVo;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("adminapi/wxoplatform/")
@SaCheckLogin
public class WeappVersionController {

    @Resource
    IWeappVersionService weappVersionService;


    /**
     * 获取最后一次提交记录
     *
     * @return
     */
    @GetMapping("weapp/commit/last")
    public Result<WxOplatfromWeappVersionVo> lastCommitRecord(){
        return Result.success(weappVersionService.getLastCommitRecord());
    }

    /**
     * 获取小程序提交记录
     *
     * @param pageParam
     * @return
     */
    @GetMapping("weapp/weapp/commit")
    public Result<PageResult<WxOplatfromWeappVersionVo>> commitRecord(@Validated PageParam pageParam){
        return Result.success(weappVersionService.list(pageParam));
    }

    /**
     * 添加小程序版本
     *
     * @return
     */
    @PostMapping("weapp/version/commit")
    public Result weappCommit(){

        return Result.success();
    }
}
