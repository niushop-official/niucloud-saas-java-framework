package com.niu.shop.enums.marketing;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ShopManjianMemberTypeEnum {
    ALL_MEMBER("all_member", "所有会员参数"),
    SELECTED_MEMBER_LEVEL("selected_member_level", "指定会员等级"),
    SELECTED_MEMBER_LABEL("selected_member_label", "指定会员标签"),
    ;

    private final String type;
    private final String name;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(ShopManjianMemberTypeEnum item : ShopManjianMemberTypeEnum.values())
        {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param type
     * @return
     */
    public static String getNameByType(String type){
        for(ShopManjianMemberTypeEnum item : ShopManjianMemberTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }


}
