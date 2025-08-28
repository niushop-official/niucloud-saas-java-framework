package com.niu.shop.enums.goods;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum GoodsBatchSetEnum {
    LABEL("label", "商品标签"),
    SERVICE("service", "商品服务"),
    VIRTUAL_SALE_NUM("virtual_sale_num", "虚拟销量"),
    CATEGORY("category", "商品分类"),
    BRAND("brand", "商品品牌"),
    POSTER("poster", "商品海报"),
    GIFT("gift", "是否赠品"),
    DELIVERY("delivery", "配送设置"),
    STOCK("stock", "商品库存"),
    DIY_FORM("diy_form", "万能表单");

    private final String key;
    private final String description;

    public static String getNameByKey(String key) {
        for (GoodsBatchSetEnum item : GoodsBatchSetEnum.values()) {
            if (item.getKey().equals(key)) {
                return item.getDescription();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByKey(String key) {
        Map<String, Object> map = new HashMap<>();
        for (GoodsBatchSetEnum item : GoodsBatchSetEnum.values()) {
            if (item.getKey().equals(key)) {
                map.put("key", item.getKey());
                map.put("name", item.getDescription());
            }
        }
        return map;
    }

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (GoodsBatchSetEnum item : GoodsBatchSetEnum.values()) {
            map.put(item.getKey(), item.getDescription());
        }
        return map;
    }
}
