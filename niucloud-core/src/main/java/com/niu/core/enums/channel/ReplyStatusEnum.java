package com.niu.core.enums.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 站点账户枚举类
 */
@Getter
@AllArgsConstructor
public enum ReplyStatusEnum {
    STATUS_ON("启用", 1),
    STATUS_OFF("关闭", 0)
    ;
    private final String name;
    private final Integer code;

    public static Map<Integer, String> getMap(){
        Map<Integer, String> map = new HashMap<>();
        for(ReplyStatusEnum item : ReplyStatusEnum.values())
        {
            map.put(item.getCode(), item.getName());
        }
        return map;
    }
}
