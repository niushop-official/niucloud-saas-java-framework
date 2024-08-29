package com.niu.core.service.admin.addon.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 插件日志参数
 */
@Data
public class AddonDevelopAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "title参数不能为空")
    @NotNull(message = "title参数缺失")
    private String title;

    @NotNull(message = "desc参数缺失")
    private String desc;

    @NotBlank(message = "icon参数不能为空")
    @NotNull(message = "icon参数缺失")
    private String icon;

    @NotBlank(message = "cover参数不能为空")
    @NotNull(message = "cover参数缺失")
    private String cover;

    @NotBlank(message = "key参数不能为空")
    @NotNull(message = "key参数缺失")
    private String key;

    @NotBlank(message = "author参数不能为空")
    @NotNull(message = "author参数缺失")
    private String author;

    @NotBlank(message = "version参数不能为空")
    @NotNull(message = "version参数缺失")
    private String version;

    @NotBlank(message = "type参数不能为空")
    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "support_app参数缺失")
    private String supportApp;

}
