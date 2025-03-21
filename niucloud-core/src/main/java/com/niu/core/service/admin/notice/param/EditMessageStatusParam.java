package com.niu.core.service.admin.notice.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class EditMessageStatusParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "key参数缺失")
    @NotEmpty(message = "key参数不能为空")
    private String key;

    @NotNull(message = "type参数缺失")
    @NotEmpty(message = "type参数不能为空")
    private String type;

    @NotNull(message = "status参数缺失")
    private Integer status;
}
