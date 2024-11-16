package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderLogActionEnum {
    ORDER_CREATE_ACTION("订单创建", "order_create"),
    ORDER_PAY_ACTION("订单支付", "order_pay"),
    ORDER_CLOSE_ACTION("订单关闭", "order_close"),
    ORDER_DELIVERY_ACTION("订单发货", "order_delivery"),
    ORDER_FINISH_ACTION("订单完成", "order_finish"),
    ORDER_REMARK_ACTION("订单商家备注", "order_remark"),
    ORDER_CLOSE_ALLOW_REFUND_ACTION("订单自动关闭售后", "order_close_allow_refund"),
    ORDER_EDIT_PRICE_ACTION("订单改价", "order_edit");

    private final String name;

    private final String action;

    public static String getNameByType(String action){
        for (OrderLogActionEnum item: OrderLogActionEnum.values()) {
            if(item.getAction().equals(action))
            {
                return item.getName();
            }
        }
        return "";
    }
}
