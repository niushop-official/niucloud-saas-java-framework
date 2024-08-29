package com.niu.core.enums.sys;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsStatusEnum {

    SENDING("sending", "发送中"),
    SUCCESS("success", "成功"),
    FAIL("fail", "失败")
    ;

    private String status;
    private  String name;

    /**
     * 通过status获取当前名称
     * @param status
     * @return
     */
    public static String getNameByStatus(String status){
        for(SmsStatusEnum item : SmsStatusEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
