package com.niu.core.service.admin.sys.param;

import lombok.Data;
import lombok.NonNull;

import jakarta.validation.constraints.NotNull;

@Data
public class SysMapParam {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "key参数缺失")
    private String key;

    @NotNull(message = "is_open参数缺失")
    private Integer isOpen;

    @NotNull(message = "valid_time参数缺失")
    private Integer validTime;
}
