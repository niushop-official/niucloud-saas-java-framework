package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor


/**
 * 退款状态
 */
public enum RefundSatusEnum {
    NOT_REFUND("不存在退款", 1),
    PORTION_REFUND("部分退款", 2),
    ALL_REFUND("全部退款", 3);

    private final String name;

    private final Integer status;

    public static String getNameByType(Integer status){
        for (RefundSatusEnum item: RefundSatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }

}
