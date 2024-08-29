package com.niu.core.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 渠道枚举类
 */
@Getter
@AllArgsConstructor
public enum ChannelEnum {
    PC("PC", "pc"),
    H5("H5", "h5"),
    WECHAT("微信公众号", "wechat"),
    WEAPP("微信小程序", "weapp"),
    APP("APP", "app"),
    ;
    private final String name;
    private final String code;

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(ChannelEnum item : ChannelEnum.values())
        {
            map.put(item.getCode(), item.getName());
        }
        return map;
    }

    /**
     * 通过code获取当前名称
     * @param code
     * @return
     */
    public static String getNameByCode(String code){
        for(ChannelEnum item : ChannelEnum.values())
        {
            if(item.getCode().equals(code))
            {
                return item.getName();
            }
        }
        return "";
    }
}
