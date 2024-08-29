package com.niu.core.enums.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 证书枚举类
 */
@Getter
@AllArgsConstructor
public enum CertEnum {
    WECHATPAY("wechatpay"),
    WECHAT("wechat"),
    ALIYUN("aliyun"),
    WEAPP("weapp"),
    ALIAPP("aliapp"),
    ;
    private final String code;

    public static List<String> getList(){
        List<String> list = new ArrayList<>();
        for(CertEnum item : CertEnum.values())
        {
            list.add(item.getCode());
        }
        return list;
    }
}
