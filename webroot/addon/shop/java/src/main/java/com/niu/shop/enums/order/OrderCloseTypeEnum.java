package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderCloseTypeEnum {
    SHOP_CLOSE("shop_close","卖家关闭"),
    BUYER_CLOSE("buyer_close","买家关闭"),
    AUTO_CLOSE("auto_close","自动关闭"),
    REFUND_CLOSE("refund_close","退款完成关闭");

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
