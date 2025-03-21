package com.niu.core.service.api.wechat.param;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class WechatAuthDataLoginParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String data;

    private Integer pid;
}
