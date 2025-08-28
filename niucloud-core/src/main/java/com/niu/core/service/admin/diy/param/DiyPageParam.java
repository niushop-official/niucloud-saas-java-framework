package com.niu.core.service.admin.diy.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 自定义页面参数
*/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiyPageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "pageTitle参数缺失")
    private String pageTitle;

    @NotNull(message = "title参数缺失")
    private String title;

    @NotNull(message = "name参数缺失")
    private String name;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "template参数缺失")
    private String template;

    @NotNull(message = "mode参数缺失")
    private String mode = "diy";

    @NotBlank(message = "value不能为空")
    @NotNull(message = "value参数缺失")
    private String value;

    @NotNull(message = "isDefault参数缺失")
    private Integer isDefault = 0;

    @NotNull(message = "isChange参数缺失")
    private Integer isChange = 0;

    private Integer siteId;

}
