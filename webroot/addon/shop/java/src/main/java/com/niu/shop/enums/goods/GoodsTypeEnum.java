package com.niu.shop.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum GoodsTypeEnum {
    REAL("real","实物商品",  "物流发货", "/shop/goods/real_edit"),
    VIRTUAL("virtual","虚拟商品", "无需物流", "/shop/goods/virtual_edit");

    private final String type;

    private final String name;

    private final String desc;

    private final String path;

    public static Map<String, Map<String, Object>> getMap() {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for(GoodsTypeEnum item : GoodsTypeEnum.values())
        {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("type", item.getType());
            mapItem.put("name", item.getName());
            mapItem.put("desc", item.getDesc());
            mapItem.put("path", item.getPath());
            map.put(item.getType(), mapItem);
        }
        return map;
    }

    /**
     * 通过type获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(GoodsTypeEnum item : GoodsTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Optional<GoodsTypeEnum> getEnumByType(String type) {
        return Arrays.stream(GoodsTypeEnum.values())
                .filter(e -> e.getType().equals(type))
                .findFirst();
    }
}
