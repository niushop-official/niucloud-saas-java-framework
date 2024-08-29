package com.niu.core.enums.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 转账方式枚举
 */
@Getter
@AllArgsConstructor

public enum RefundTypeEnum {
    WECHATPAY("微信原路退款", "wechatpay", true),
    ALIPAY("支付宝原路退款", "alipay", true),
    BALANCE("退款到余额", "balance", false),
    OFFLINE("线下退款", "offline", false),
    BACK("原路退款", "back", false);

    //转账方式名称
    String name;
    //转账方式关键字
    String key;
    //是否是线上
    Boolean isOnline;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        map.put(BACK.getKey(), BACK.getName());
        map.put(OFFLINE.getKey(), OFFLINE.getName());
        return map;
    }

    /**
     * 通过type获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(RefundTypeEnum item : RefundTypeEnum.values())
        {
            if(item.getKey().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
