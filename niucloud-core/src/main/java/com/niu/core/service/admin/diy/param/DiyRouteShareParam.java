package com.niu.core.service.admin.diy.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 自定义路由参数
*/
@Data
public class DiyRouteShareParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "title参数缺失")
    private String title;

    @NotNull(message = "name参数缺失")
    private String name;

    @NotNull(message = "page参数缺失")
    private String page;

    @NotNull(message = "share参数缺失")
    private String share;

    @NotNull(message = "isShare参数缺失")
    private Integer isShare;

    @NotNull(message = "sort参数缺失")
    private Integer sort = 0;

}
