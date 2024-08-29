package com.niu.core.enums.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信相关枚举
 */
@Getter
@AllArgsConstructor
public enum WechatEncryptionTypeEnum {

    NOT_ENCRYPT("not_encrypt", "明文模式"),//明文
    COMPATIBLE("compatible", "兼容模式"),//兼容
    SAFE("safe", "安全模式"),//safe
    ;

    private final String type;

    private final String title;

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(WechatEncryptionTypeEnum item : WechatEncryptionTypeEnum.values())
        {
            map.put(item.getType(), item.getTitle());
        }
        return map;
    }
}
