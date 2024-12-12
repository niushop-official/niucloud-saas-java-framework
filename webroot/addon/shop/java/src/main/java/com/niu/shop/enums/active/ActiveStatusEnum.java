package com.niu.shop.enums.active;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ActiveStatusEnum {
    NOT_ACTIVE("not_active", "未开始"),
    ACTIVE("active", "进行中"),
    END("end", "已结束"),
    CLOSE("close", "已关闭");


    private final String status;

    private final String name;


    public static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (ActiveStatusEnum item : ActiveStatusEnum.values()) {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     *
     * @param status
     * @return
     */
    public static String getNameByStatus(String status) {
        for (ActiveStatusEnum item : ActiveStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item.getName();
            }
        }
        return "";
    }

    public static ActiveStatusEnum getEnumByStatus(String status) {
        for (ActiveStatusEnum item : ActiveStatusEnum.values()) {
            if (item.getStatus().equals(status)) {
                return item;
            }
        }
        return null;
    }
}
