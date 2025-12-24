package com.niu.core.service.admin.diy.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiyDecoratePageSearchParam {

    @NotNull(message = "type不能为空")
    @NotBlank(message = "type不能为空")
    private String type;

}
