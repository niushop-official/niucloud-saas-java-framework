package com.niu.shop.enums.marketing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ShopManjianDiscountTypeEnum {
    OVER_N_YUAN("over_n_yuan", "满N元"),
    OVER_N_PIECE("over_n_piece", "满N件")
    ;

    private final String type;
    private final String name;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(ShopManjianDiscountTypeEnum item : ShopManjianDiscountTypeEnum.values())
        {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(ShopManjianDiscountTypeEnum item : ShopManjianDiscountTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }


}
