package com.niu.shop.service.core.delivery.vo;

import com.niu.shop.service.core.delivery.electronic.sheet.engine.ApplyResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ElectronicSheetPrintVo extends ApplyResult {
    private static final ElectronicSheetPrintVo FAILED_RESULT = new ElectronicSheetPrintVo();

    private Integer deliveryId;

    static {
        FAILED_RESULT.setSuccess(false);
        FAILED_RESULT.setReason("该订单不支持打印电子面单");
        FAILED_RESULT.setResultCode(-1);
    }

    public static ElectronicSheetPrintVo getFailedResult() {
        return FAILED_RESULT;
    }
}
