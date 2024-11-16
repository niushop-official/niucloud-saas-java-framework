package com.niu.shop.service.core.delivery.electronic.sheet.engine;

import lombok.Data;

@Data
public class ApplyResult {
    private boolean success;
    private String reason;
    private Integer resultCode;
    private String printTemplate;
    private Object orderInfo;
    private String orderNo;
}
