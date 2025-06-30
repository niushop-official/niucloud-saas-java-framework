package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor


/**
 * 订单配送状态
 */
public enum DeliveryStatusEnum {


    HAVE_DELIVERED("已配送", 1),
    NO_DELIVERED("未配送", 0);

    private final String name;

    private final Integer status;

    public static String getNameByStatus(Integer status){
        for (DeliveryStatusEnum item: DeliveryStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }


}
