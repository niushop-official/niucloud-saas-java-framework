package com.niu.core.service.core.captcha.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.niu.core.common.exception.AuthException;
import com.niu.core.service.core.captcha.ICoreCaptchaImgService;
import com.niu.core.service.core.captcha.param.CoreCaptchaValiDateParam;
import com.niu.core.service.core.captcha.vo.CoreCaptchaInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 验证码服务层
 */
@Service
public class CoreCaptchaImgServiceImpl implements ICoreCaptchaImgService {

    @Autowired
    CaptchaService captchaService;
    /**
     * 创建验证码
     *
     * @param captchaType
     * @return
     */
    @Override
    public CoreCaptchaInfoVo create(String captchaType) {
        CaptchaVO captchaVO=new CaptchaVO();
        captchaVO.setCaptchaType(captchaType);
        ResponseModel responseModel=captchaService.get(captchaVO);
        CoreCaptchaInfoVo coreCaptchaInfoVo=new CoreCaptchaInfoVo();
        if(responseModel.isSuccess()){
            JSONObject jsonObject=JSONUtil.parseObj(responseModel.getRepData());
            coreCaptchaInfoVo.setToken(jsonObject.getStr("token"));
            coreCaptchaInfoVo.setJigsawImageBase64(jsonObject.getStr("jigsawImageBase64"));
            coreCaptchaInfoVo.setOriginalImageBase64(jsonObject.getStr("originalImageBase64"));
            coreCaptchaInfoVo.setSecretKey(jsonObject.getStr("secretKey"));
        }else{
            throw new AuthException(responseModel.getRepMsg());
        }
        return coreCaptchaInfoVo;
    }

    /**
     * 一次验证验证码
     *
     * @param coreCaptchaValiDateParam
     * @return
     */
    @Override
    public Boolean check(CoreCaptchaValiDateParam coreCaptchaValiDateParam) {
        if(ObjectUtil.isNull(coreCaptchaValiDateParam.getCaptchaType()) || ObjectUtil.isNull(coreCaptchaValiDateParam.getCaptchaCode()) || ObjectUtil.isNull(coreCaptchaValiDateParam.getCaptchaKey())){
            throw new AuthException("缺少参数");
        }
        CaptchaVO captchaVO=new CaptchaVO();
        captchaVO.setCaptchaType(coreCaptchaValiDateParam.getCaptchaType());
        captchaVO.setToken(coreCaptchaValiDateParam.getCaptchaKey());
        captchaVO.setPointJson(coreCaptchaValiDateParam.getCaptchaCode());
        ResponseModel responseModel=captchaService.check(captchaVO);
        if(!responseModel.isSuccess()){
            throw new AuthException(responseModel.getRepMsg());
        }
        return true;
    }

    /**
     * 二次验证 验证码
     *
     * @param captchaVO
     */
    @Override
    public void verification(CaptchaVO captchaVO) {
        ResponseModel responseModel=captchaService.verification(captchaVO);
        if(!responseModel.isSuccess()){
            throw new AuthException(responseModel.getRepMsg());
        }
    }
}
