package com.niu.core.enums.notice;

import com.niu.core.enums.common.ChannelEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知类型
 */

@Getter
@AllArgsConstructor
public enum NoticeTypeEnum {
    SMS("短信", "sms"),
    WECHAT("微信公众号", "wechat"),
    WEAPP("微信小程序", "weapp")
    ;

    private final String name;

    private final String code;

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(NoticeTypeEnum item : NoticeTypeEnum.values())
        {
            map.put(item.getCode(), item.getName());
        }
        return map;
    }

    public static String getNameByType(String type) {
        for(NoticeTypeEnum item : NoticeTypeEnum.values())
        {
            if (item.code.equals(type)) return item.getName();
        }
        return "";
    }

}
