package com.niu.core.enums.sys;

import com.niu.core.common.utils.language.LanguageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 导出状态
 */
@Getter
@AllArgsConstructor
public enum ExportEnum {

    EXPORTING(LanguageUtils.get("dict_export.status_exporting"), 1),
    SUCCESS(LanguageUtils.get("dict_export.status_success"), 1),
    FAIL(LanguageUtils.get("dict_export.status_fail"), 1)
    ;

    private final String name;
    private final Integer code;


    /**
     * 获取状态
     *
     * @return
     */
    public static Map<Integer, String> getStatus() {
        Map<Integer, String> map = new HashMap<>();
        for(ExportEnum item : ExportEnum.values()){
            map.put(item.getCode(), item.getName());
        }
        return map;
    }
}
