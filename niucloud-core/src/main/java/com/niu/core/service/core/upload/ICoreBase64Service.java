package com.niu.core.service.core.upload;

public interface ICoreBase64Service {

    /**
     * base64图片上传
     * @param content
     * @param siteId
     * @return
     */
    String image(String content, Integer siteId);
}
