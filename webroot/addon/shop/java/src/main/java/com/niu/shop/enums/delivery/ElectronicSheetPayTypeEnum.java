package com.niu.shop.enums.delivery;

import lombok.Getter;

@Getter
public enum ElectronicSheetPayTypeEnum {
    
    CASH_PAYMENT("现付"),
    FREIGHT_COLLECT("到付"),
    MONTHLY_STATEMENT("月结");

    private final String payType;

    ElectronicSheetPayTypeEnum(String payType) {
        this.payType = payType;
    }
}
