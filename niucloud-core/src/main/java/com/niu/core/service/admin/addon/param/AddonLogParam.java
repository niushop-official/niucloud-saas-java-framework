package com.niu.core.service.admin.addon.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 插件日志参数
*/
@Data
public class AddonLogParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String action;

    @NotNull(message = "key参数缺失")
    private String key;

    @NotNull(message = "fromVersion参数缺失")
    private String fromVersion;

    @NotNull(message = "toVersion参数缺失")
    private String toVersion;

}
