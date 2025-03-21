package com.niu.core.service.core.channel.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 支付宝小程序配置视图
 */

@Data
public class SetH5Param implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "is_open参数缺失")
    private Integer isOpen;

}