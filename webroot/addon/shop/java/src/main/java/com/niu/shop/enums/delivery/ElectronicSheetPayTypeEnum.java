package com.niu.shop.enums.delivery;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ElectronicSheetPayTypeEnum {

    CASH_PAYMENT(1, "现付"),
    FREIGHT_COLLECT(2, "到付"),
    MONTHLY_STATEMENT(3, "月结");

    private final int value;
    private final String payType;

    ElectronicSheetPayTypeEnum(int value, String payType) {
        this.value = value;
        this.payType = payType;
    }

    public static String getPayTypeByType(Integer type) {
        return Arrays.stream(ElectronicSheetPayTypeEnum.values())
                .filter(t -> ObjectUtil.equal(t.getValue(), type))
                .map(ElectronicSheetPayTypeEnum::getPayType)
                .findFirst()
                .orElse("");
    }
}
