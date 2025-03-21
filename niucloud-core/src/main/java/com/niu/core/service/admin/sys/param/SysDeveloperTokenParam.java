package com.niu.core.service.admin.sys.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class SysDeveloperTokenParam {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "token参数缺失")
    private String token;

}
