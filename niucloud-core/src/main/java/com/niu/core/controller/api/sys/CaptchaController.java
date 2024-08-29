package com.niu.core.controller.api.sys;

import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.system.CaptchaUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CaptchaController {

    @GetMapping("/captcha")
    public Result<CaptchaUtils.Captcha> captcha() {
        return Result.success(CaptchaUtils.create());
    }

}
