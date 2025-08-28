package com.niu.core.enums.notice;

import com.niu.core.enums.common.ChannelEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static List<Map<String, Object>> getSignSource() {
        List<Map<String, Object>> sources = new ArrayList<>();
        sources.add(createMap(1, "企业名称"));
        sources.add(createMap(2, "事业单位"));
        sources.add(createMap(3, "商标"));
        sources.add(createMap(4, "APP"));
        sources.add(createMap(5, "小程序"));
        return sources;
    }

    public static List<Map<String, Object>> getSignType() {
        List<Map<String, Object>> types = new ArrayList<>();
        types.add(createMap(0, "全称"));
        types.add(createMap(1, "简称"));
        return types;
    }

    public static List<Map<String, Object>> getSignDefault() {
        List<Map<String, Object>> defaults = new ArrayList<>();
        defaults.add(createMap(0, "否"));
        defaults.add(createMap(1, "是"));
        return defaults;
    }

    private static Map<String, Object> createMap(int type, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("name", name);
        return map;
    }


}
