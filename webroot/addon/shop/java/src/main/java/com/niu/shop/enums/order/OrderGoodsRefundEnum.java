package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderGoodsRefundEnum {
    NORMAL("正常", 1),
    REFUNDING("退款中", 2),
    REFUND_FINISH("退款完成", 3);

    private final String name;

    private final Integer status;

    public static String getNameByStatus(Integer status){
        for (OrderGoodsRefundEnum item: OrderGoodsRefundEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status){
        Map<String, Object> map = new HashMap<>();
        for (OrderGoodsRefundEnum item: OrderGoodsRefundEnum.values()) {
            if(item.getStatus().equals(status))
            {
                map.put("name", item.getName());
                map.put("status", item.getStatus());
            }
        }
        return map;
    }

    public static Map<Integer, Map<String, Object>> getMap() {
        Map<Integer, Map<String, Object>> map = new HashMap<>();
        for (OrderGoodsRefundEnum item : OrderGoodsRefundEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("name", item.getName());
            mapItem.put("status", item.getStatus());
            map.put(item.getStatus(), mapItem);
        }
        return map;
    }
}
