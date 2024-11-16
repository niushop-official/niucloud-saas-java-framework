package com.niu.shop.enums.active;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActiveGoodsTypeEnum {
    GOODS_SINGLE("single","单品"),
    GOODS_INDEPENDENT("independent","独立商品"),
    GOODS_SHOP("shop", "店铺整体商品");

    private final String type;

    private final String name;

    /**
     * 通过type获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(ActiveGoodsTypeEnum item : ActiveGoodsTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
