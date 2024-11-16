package com.niu.shop.enums.marketing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShopPointExchangeStatusEnum {
    UP(1, "已上架"),
    DOWN(0, "已下架"),
    DELETE(-1, "已删除"),
    ;

    private final Integer status;
    private final String name;

    public static String getNameByStatus(Integer status){
        for(ShopPointExchangeStatusEnum item : ShopPointExchangeStatusEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }

}
