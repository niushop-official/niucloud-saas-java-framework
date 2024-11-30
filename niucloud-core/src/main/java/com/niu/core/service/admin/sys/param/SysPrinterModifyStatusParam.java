package com.niu.core.service.admin.sys.param;

import lombok.Data;

@Data
public class SysPrinterModifyStatusParam {
    private Integer printerId;
    private Integer status;
}
