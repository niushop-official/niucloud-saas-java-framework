package com.niu.core.enums.sys;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 导出状态
 */
@Getter
@AllArgsConstructor
public enum ExportEnum {

    EXPORTING("导出中", 1),
    SUCCESS("导出成功", 2),
    FAIL("导出失败", -1);

    private final String name;
    private final Integer code;


    /**
     * 获取状态
     */
    public static Map<Integer, String> getStatus() {
        Map<Integer, String> map = new HashMap<>();
        for (ExportEnum item : ExportEnum.values()) {
            map.put(item.getCode(), item.getName());
        }
        return map;
    }

    public static String getNameByCode(Integer code) {
        return Arrays.stream(ExportEnum.values())
                .filter(s -> ObjectUtil.equal(s.getCode(), code))
                .findFirst()
                .map(ExportEnum::getName)
                .orElse("");
    }
}
