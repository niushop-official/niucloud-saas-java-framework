package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MemberLoginTypeEnum {
    WEAPP("微信小程序授权登录", "weapp"),
    WECHAT("微信公众号授权登录", "wechat"),
    USERNAME("用户名密码登录", "username"),
    MOBILE("手机验证码登录", "mobile");

    private final String name;

    private final String type;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(MemberLoginTypeEnum item : MemberLoginTypeEnum.values())
        {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过type获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(MemberLoginTypeEnum item : MemberLoginTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
