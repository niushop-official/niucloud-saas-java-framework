package com.niu.shop.enums.marketing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShopPointExchangeTypeEnum {
    GOODS("goods", "商品"),
    COUPON("coupon", "优惠卷"),
    BALANCE("balance", "余额"),
    ;

    private final String type;
    private final String name;


    public static String getNameByType(String type){
        for(ShopPointExchangeTypeEnum item : ShopPointExchangeTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }

}
