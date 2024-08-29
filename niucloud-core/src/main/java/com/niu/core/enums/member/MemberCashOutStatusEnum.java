package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MemberCashOutStatusEnum {
    WAIT_AUDIT("待审核", 1),
    WAIT_TRANSFER("待转账", 2),
    TRANSFERED("已转账", 3),
    REFUSE("已拒绝", -1),
    CANCEL("已取消", -2);

    private final String name;

    private final Integer status;

    public static Map<Integer, String> getMap() {
        Map<Integer, String> map = new HashMap<>();
        for(MemberCashOutStatusEnum item : MemberCashOutStatusEnum.values())
        {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param status
     * @return
     */
    public static String getNameByStatus(Integer status){
        for(MemberCashOutStatusEnum item : MemberCashOutStatusEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
