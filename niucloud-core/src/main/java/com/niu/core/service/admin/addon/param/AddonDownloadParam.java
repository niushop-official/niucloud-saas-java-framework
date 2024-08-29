package com.niu.core.service.admin.addon.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 */
@Data
public class AddonDownloadParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "version参数不能为空")
    @NotNull(message = "version参数缺失")
    private String version;

}
