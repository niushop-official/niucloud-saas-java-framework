package com.niu.core.enums.generator;

import com.niu.core.enums.site.SiteStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum SqlColumnEnum {
    STR("String", new String[]{"char", "varchar", "nvarchar", "varchar2","datetime", "time", "date", "text", "longText"}),
    LONG("Long", new String[]{"timestamp"}),
    INT("Integer", new String[]{"tinyint", "smallint", "mediumint", "int", "number", "integer", "bit", "bigint"}),
    COMMISSION("BigDecimal", new String[]{"float", "double", "decimal"}),
    GROWTH("Boolean", new String[]{"bool", "boolean"});

    private final String field;

    private final String[] columns;

    public static Map<String, String[]> getMap() {
        Map<String, String[]> map = new HashMap<>();
        for(SqlColumnEnum item : SqlColumnEnum.values())
        {
            map.put(item.getField(), item.getColumns());
        }
        return map;
    }

}
