package com.niu.shop.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum GoodsEvaluateEnum {
    AUDIT_NO("无需审核", 0),
    AUDIT("待审核", 1),
    AUDIT_ADOPT("审核通过", 2),
    AUDIT_REFUSE("审核拒绝", 3);

    private final String name;

    private final Integer status;

    public static String getNameByStatus(Integer status){
        for (GoodsEvaluateEnum item: GoodsEvaluateEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status){
        Map<String, Object> map = new HashMap<>();
        for (GoodsEvaluateEnum item: GoodsEvaluateEnum.values()) {
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
        for (GoodsEvaluateEnum item : GoodsEvaluateEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("name", item.getName());
            mapItem.put("status", item.getStatus());
            map.put(item.getStatus(), mapItem);
        }
        return map;
    }
}
