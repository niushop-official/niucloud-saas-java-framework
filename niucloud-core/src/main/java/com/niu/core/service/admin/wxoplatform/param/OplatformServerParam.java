package com.niu.core.service.admin.wxoplatform.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class OplatformServerParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestBody;

    private String timestamp;

    private String nonce;

    private String signature;

    private String encryptType;

    private String msgSignature;
}
