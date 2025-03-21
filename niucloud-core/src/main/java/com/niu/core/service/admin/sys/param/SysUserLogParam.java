package com.niu.core.service.admin.sys.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 管理员操作记录参数
*/
@Data
public class SysUserLogParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ip参数缺失")
    private String ip;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "uid参数缺失")
    private Integer uid;

    @NotNull(message = "username参数缺失")
    private String username;

    @NotNull(message = "url参数缺失")
    private String url;

    @NotNull(message = "params参数缺失")
    private String params;

    @NotNull(message = "type参数缺失")
    private String type;

}
