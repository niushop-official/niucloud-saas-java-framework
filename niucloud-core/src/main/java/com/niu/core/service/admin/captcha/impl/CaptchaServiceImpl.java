package com.niu.core.service.admin.captcha.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.captcha.ICaptchaService;
import org.springframework.stereotype.Service;

@Service
public class CaptchaServiceImpl implements ICaptchaService {
    @Override
    public JSONObject create(String captchaType) {
        return null;
    }

    @Override
    public Boolean check(String captchaKey, String captchaCode) {
        return null;
    }
}
