package com.niu.core.enums.pay;

import com.niu.core.enums.member.MemberLoginTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 转账方式枚举
 */
@Getter
@AllArgsConstructor

public enum TransferTypeEnum {
    WECHATPAY("微信", "wechatpay", true),
    ALIPAY("支付宝", "alipay", false),
    BALANCEPAY("银行卡", "bank", false),
    OFFLINE("线下转账", "offline", false);

    //转账方式名称
    String name;
    //转账方式关键字
    String key;
    //是否是线上
    Boolean isOnline;

    public static Map<String, Map> getMap() {
        Map<String, Map> map = new HashMap<>();
        for(TransferTypeEnum item : TransferTypeEnum.values())
        {
            Map vo = new HashMap();
            vo.put("name", item.getName());
            vo.put("key", item.getKey());
            vo.put("is_online", item.getIsOnline());
            map.put(item.getKey(), vo);
        }
        return map;
    }

    /**
     * 通过type获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(TransferTypeEnum item : TransferTypeEnum.values())
        {
            if(item.getKey().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
