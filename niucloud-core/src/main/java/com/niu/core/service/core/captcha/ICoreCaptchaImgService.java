package com.niu.core.service.core.captcha;

import com.anji.captcha.model.vo.CaptchaVO;
import com.niu.core.service.core.captcha.param.CoreCaptchaValiDateParam;
import com.niu.core.service.core.captcha.vo.CoreCaptchaInfoVo;

/**
 * 验证码服务层
 *
 */
public interface ICoreCaptchaImgService {

    /**
     * 创建验证码
     *
     * @param captchaType
     * @return
     */
    CoreCaptchaInfoVo create(String captchaType);

    /**
     * 一次验证验证码
     *
     * @param coreCaptchaValiDateParam
     * @return
     */
    Boolean check(CoreCaptchaValiDateParam coreCaptchaValiDateParam);

    /**
     * 二次验证 验证码
     *
     * @param captchaVO
     */
    void verification(CaptchaVO captchaVO);
}
