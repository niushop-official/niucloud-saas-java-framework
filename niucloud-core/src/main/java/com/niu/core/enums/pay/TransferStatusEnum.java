package com.niu.core.enums.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付状态枚举
 */
@Getter
@AllArgsConstructor
public enum TransferStatusEnum {
    WAIT("待转账", "wait"),
    DEALING("处理中", "dealing"),
    SUCCESS("转账成功", "success"),
    FAIL("转账失败", "fail");

    private final String name;
    private final String status;

    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for(TransferStatusEnum item : TransferStatusEnum.values())
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
    public static String getNameByStatus(String status){
        for(TransferStatusEnum item : TransferStatusEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
