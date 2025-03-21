package com.niu.core.common.component.upload;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.niu.core.common.component.upload.annotation.UploadProvider;

public abstract class AbstractUploadProvider {

    // 访问的域
    protected String domain;

    /**
     * 获取注解（UploadProvider）的上传方式名称（name）
     *
     * @return
     */
    public String getUploadMethod() {
        UploadProvider uploadProvider = this.getClass().getAnnotation(UploadProvider.class);
        if (uploadProvider != null) {
            return uploadProvider.name();
        }
        return "local";
    }

    /**
     * 获取访问地址
     *
     * @param location
     * @return
     */
    public String getAccessUrl(String location) {
        if (ObjectUtil.isNotEmpty(location) && ObjectUtil.isNotEmpty(domain)) {
            return domain + "/" + location;
        }
        return location;
    }

}
