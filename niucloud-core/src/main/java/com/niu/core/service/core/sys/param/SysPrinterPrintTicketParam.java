package com.niu.core.service.core.sys.param;

import lombok.Data;

@Data
public class SysPrinterPrintTicketParam {
    private Integer siteId;
    private String type;
    private String trigger;
    private Business business;

    @Data
    public static class Business {
        private Integer orderId;
    }
}
