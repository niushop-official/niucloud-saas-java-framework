package com.niu.core.service.core.sys.param;

import lombok.Data;

@Data
public class SysPrinterAddPrinterYlyParam {
    private String brand;
    private String printerName;
    private String printerCode;
    private String printerKey;
    private String openId;
    private String apikey;
    private String templateType;
    private String trigger;
    private String value;
    private String printWidth;
    private Integer status;
}
