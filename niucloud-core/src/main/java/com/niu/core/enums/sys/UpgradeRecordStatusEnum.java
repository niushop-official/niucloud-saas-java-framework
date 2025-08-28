package com.niu.core.enums.sys;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UpgradeRecordStatusEnum {

    STATUS_READY("ready", "升级中"),
    STATUS_COMPLETE("complete", "完成"),
    STATUS_FAIL("fail", "失败"),
    STATUS_CANCEL("cancel", "已取消")
    ;

    private String status;
    private  String name;

    /**
     * 通过status获取当前名称
     * @param status
     * @return
     */
    public static String getNameByStatus(String status){
        for(UpgradeRecordStatusEnum item : UpgradeRecordStatusEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
