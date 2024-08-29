package com.niu.core.enums.member;

import com.niu.core.enums.site.SiteStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum AccountTypeEnum {
    POINT("积分", "point"),
    BALANCE("余额", "balance"),
    MONEY("可提现余额", "money"),
    COMMISSION("佣金", "commission"),
    GROWTH("成长值", "growth");

    private final String name;

    private final String type;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(AccountTypeEnum item : AccountTypeEnum.values())
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
        for(AccountTypeEnum item : AccountTypeEnum.values())
        {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
