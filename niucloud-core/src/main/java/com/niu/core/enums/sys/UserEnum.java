package com.niu.core.enums.sys;

import com.niu.core.enums.site.SiteStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserEnum {

    ON("正常",1),
    OFF("无效", 0),
    ;
    private final String name;
    private final Integer code;

    /**
     * 通过code获取当前名称
     * @param code
     * @return
     */
    public static String getNameByCode(Integer code){
        for(UserEnum item : UserEnum.values())
        {
            if(item.getCode()==code)
            {
                return item.getName();
            }
        }
        return "";
    }
}
