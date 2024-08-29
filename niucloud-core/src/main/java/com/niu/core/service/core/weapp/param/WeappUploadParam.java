package com.niu.core.service.core.weapp.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 微信小程序上传
 */

@Data
public class WeappUploadParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String baseUrl; // 小程序名称
    private String appId; // APPID
    private String uploadPrivateKey;
    private Integer siteId; // 权限设置
    private String version;
    private String desc;
    private List<String> addon;
}