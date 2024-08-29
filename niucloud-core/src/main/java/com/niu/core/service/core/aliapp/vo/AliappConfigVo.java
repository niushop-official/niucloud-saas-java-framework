package com.niu.core.service.core.aliapp.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 支付宝小程序配置视图
 */

@Data
public class AliappConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name = "";
    private String appId = ""; // 原始ID
    private String privateKey = ""; // 秘钥
    private String aesKey = "";
    private String publicKeyCrt = "";
    private String alipayPublicKeyCrt = "";
    private String alipayWithCrt = "";
    private String qrcode = "";

}