package com.niu.core.service.admin.sys.param;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class SysUpgradeRecordsDelParam {

    /**
     * 删除ID号
     */
    @NotNull(message = "删除ID号不能为空")
    private Object ids;

}
