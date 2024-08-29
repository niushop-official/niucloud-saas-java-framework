package com.niu.core.enums.site;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 站点账户枚举类
 */
@Getter
@AllArgsConstructor
public enum SiteAccountLogEnum {
    PAY("支付", "pay"),
    REFUND("退款", "refund"),
    TRANSFER("转账", "transfer"),
    ;
    private final String name;
    private final String code;

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(SiteAccountLogEnum item : SiteAccountLogEnum.values())
        {
            map.put(item.getCode(), item.getName());
        }
        return map;
    }

    public static void main(String[] args){
        System.out.println(getKeyValuePairs(SiteAccountLogEnum.class));
    }

    public static Map<String, String> getKeyValuePairs(Class<? extends Enum> e) {
        System.out.println(e.getName());
        try {
            System.out.println(e.getDeclaredMethod("getCode"));
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        Map<String, String> keyValuePairs = new HashMap<>();
        for (Object constant : e.getEnumConstants()) {
            Enum enumConst = (Enum) constant;
            try {
                System.out.println(e.getDeclaredMethod("getCode"));
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }
            // System.out.println(enumConst.name(),.getValue());
        }
        return keyValuePairs;
    }

}
