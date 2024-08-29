package com.niu.core.controller.adminapi.login;

import com.alibaba.fastjson2.JSONArray;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.captcha.ICaptchaService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.core.captcha.ICoreCaptchaImgService;
import com.niu.core.service.core.captcha.param.CoreCaptchaValiDateParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 图文验证码控制器
 */
@RestController
@RequestMapping("/adminapi/captcha")
public class CaptchaController {

    @Resource
    ICoreCaptchaImgService coreCaptchaImgService;


    /**
     * 创建验证码
     *
     * @param captchaType
     * @return
     */
    @GetMapping("/create")
    public Result create(@Validated @RequestParam("captchaType") String captchaType){
        return Result.success(coreCaptchaImgService.create(captchaType));
    }


    /**
     * 一次验证验证码
     *
     * @param coreCaptchaValiDateParam
     * @return
     */
    @GetMapping("/check")
    public Result check(@Validated CoreCaptchaValiDateParam coreCaptchaValiDateParam){

        return Result.success(coreCaptchaImgService.check(coreCaptchaValiDateParam));
    }


}
