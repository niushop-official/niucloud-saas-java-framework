package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MemberRegisterTypeEnum {
    WEAPP("微信小程序", "weapp"),
    WECHAT("公众号", "wechat"),
    USERNAME("用户名密码注册", "username"),
    MOBILE("手机验证码注册", "mobile"),
    MANUAL("商家添加", "manual");

    private final String name;

    private final String type;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(MemberRegisterTypeEnum item : MemberRegisterTypeEnum.values())
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
        for(MemberRegisterTypeEnum item : MemberRegisterTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }

}
