package com.niu.core.service.api.wechat.param;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class WechatSyncParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "code参数缺失")
    @NotBlank(message = "code不能为空")
    private String code;
}
