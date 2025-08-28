package com.niu.core.service.admin.sys.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysBackupRecordsUpdateParam {

    @NotNull(message = "ID不能为空")
    private Integer id;

    @NotNull(message = "备注内容不能为空")
    private String remark;

}
