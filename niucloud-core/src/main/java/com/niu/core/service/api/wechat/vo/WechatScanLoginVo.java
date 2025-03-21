package com.niu.core.service.api.wechat.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class WechatScanLoginVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String url;

    private String key;
}
