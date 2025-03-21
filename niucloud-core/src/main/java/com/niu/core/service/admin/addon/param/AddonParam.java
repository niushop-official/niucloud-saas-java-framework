package com.niu.core.service.admin.addon.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 插件参数
*/
@Data
public class AddonParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "title参数缺失")
    private String title;

    @NotNull(message = "icon参数缺失")
    private String icon;

    @NotNull(message = "key参数缺失")
    private String key;

    private String desc;

    private Integer status;

    @NotNull(message = "author参数缺失")
    private String author;

    @NotNull(message = "version参数缺失")
    private String version;

    private Long installTime;

    @NotNull(message = "cover参数缺失")
    private String cover;

    @NotNull(message = "type参数缺失")
    private String type;

    private String supportApp;

    private Integer isStar;

    private String[] compile;

}
