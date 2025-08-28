package com.niu.core.common.utils.system;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.niu.core.common.utils.RequestUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

public class CaptchaUtils {

    private static Cache<String, String> cache = CacheUtil.newTimedCache(300000);
    @Data
    public static class Captcha {
        private String captchaKey;
        private String img;
        private String captchaCode;
    }

    /**
     * 创建验证码
     * @return
     */
    public static Captcha create() {
        RandomGenerator randomGenerator = new RandomGenerator("0123456789", 4);
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(100, 36);
        lineCaptcha.setGenerator(randomGenerator);
        lineCaptcha.getImageBase64();

        Captcha captcha = new Captcha();
        captcha.setCaptchaKey(RandomUtil.randomString(32));
        captcha.setImg("data:image/png;base64," + lineCaptcha.getImageBase64());

        CaptchaUtils.cache.put(captcha.getCaptchaKey(), lineCaptcha.getCode());

        return captcha;
    }

    /**
     * 验证码校验
     */
    public static void check(String captchaKey, String captchaCode) {
        HttpServletRequest request = RequestUtils.handler();
        if (ObjectUtil.isEmpty(captchaKey) || ObjectUtil.isEmpty(captchaCode)) throw new RuntimeException("验证码有误");

        String cache = CaptchaUtils.cache.get(captchaKey, false);
        if (cache == null || cache.isEmpty() || !cache.equals(captchaCode)) throw new RuntimeException("验证码有误");

        CaptchaUtils.cache.remove(captchaKey);
    }
}
