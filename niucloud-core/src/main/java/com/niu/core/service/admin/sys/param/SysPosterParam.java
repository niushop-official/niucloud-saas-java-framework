package com.niu.core.service.admin.sys.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 海报参数
 */
@Data
public class SysPosterParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "name参数缺失")
    private String name;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "channel参数缺失")
    private String channel;

    @NotNull(message = "value参数缺失")
    private String value;

    @NotNull(message = "status参数缺失")
    private Integer status;

    @NotNull(message = "addon参数缺失")
    private String addon;

    @NotNull(message = "isDefault参数缺失")
    private Integer isDefault = 0;

    private Integer siteId;

}
