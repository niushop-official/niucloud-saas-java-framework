package com.niu.core.service.admin.wechat.vo;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * WechatFans视图
 */
@Data
public class WechatStaticInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serveUrl;
    private String businessDomain;
    private String jsSecureDomain;
    private String webAuthDomain;
    private Map<String, String> encryptionType;

}
