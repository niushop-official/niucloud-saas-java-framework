package com.niu.shop.enums.marketing;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ShopManjianRuleTypeEnum {
    LADDER("ladder", "阶梯优惠"),
    CYCLE("cycle", "循环优惠")
    ;

    private final String type;
    private final String name;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(ShopManjianRuleTypeEnum item : ShopManjianRuleTypeEnum.values())
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
        for(ShopManjianRuleTypeEnum item : ShopManjianRuleTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }


}
