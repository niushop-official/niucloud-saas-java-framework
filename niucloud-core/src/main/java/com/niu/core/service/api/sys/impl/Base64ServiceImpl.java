package com.niu.core.service.api.sys.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.api.sys.IBase64Service;
import com.niu.core.service.api.sys.vo.Base64ImageVo;
import com.niu.core.service.core.upload.ICoreBase64Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Base64ServiceImpl implements IBase64Service {

    @Autowired
    private ICoreBase64Service coreBase64Service;

    /**
     * base64图片上传
     * @param content
     * @return
     */
    @Override
    public Base64ImageVo image(String content) {
        String url = coreBase64Service.image(content, RequestUtils.siteId());
        Base64ImageVo base64ImageVo = new Base64ImageVo();
        base64ImageVo.setUrl(url);
        System.out.println("base64ImageVo = " + url);
        return base64ImageVo;
    }
}
