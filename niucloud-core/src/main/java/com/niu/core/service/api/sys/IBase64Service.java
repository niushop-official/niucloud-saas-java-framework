package com.niu.core.service.api.sys;


import com.niu.core.service.api.sys.vo.Base64ImageVo;

public interface IBase64Service {

    /**
     * base64图片上传
     * @param content
     * @return
     */
    Base64ImageVo image(String content);
}
