package com.niu.core.service.admin.sys.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BackupRestoreParam {

    @NotNull(message = "ID不能为空")
    private Integer id;
}
