package com.niu.core.service.admin.captcha;

import cn.hutool.json.JSONObject;
import com.google.gson.JsonObject;

public interface ICaptchaService {

    JSONObject create(String captchaType);

    Boolean check(String captchaKey, String captchaCode);


}
