package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderCloseTypeEnum {
    SHOP_CLOSE("shop_close","后台关闭"),
    BUYER_CLOSE("buyer_close","用户关闭"),
    AUTO_CLOSE("auto_close","未支付自动关闭"),
    REFUND_CLOSE("refund_close","退款完成关闭"),
    ORDER_CLOSE_ALLOW_REFUND_ACTION("order_close_allow_refund", "订单完成关闭售后");

    private final String type;

    private final String name;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(OrderCloseTypeEnum item : OrderCloseTypeEnum.values())
        {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过type获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(OrderCloseTypeEnum item : OrderCloseTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
