package com.niu.core.enums.site;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum AddonChildMenuEnum {
    
    SYSTEM_TOOL("system_tool", "系统工具", "系统", 97),
    MARKETING_TOOL("marketing_tool", "营销工具", "工具", 99),
    MARKETING_ACTIVE("marketing_active", "营销活动", "活动", 100),
    ADDON_TOOL("addon_tool", "应用插件", "插件", 98);
    
    private final String key;
    private final String name;
    private final String shortName;
    private final Integer sort;

    /**
     * 获取所有菜单配置
     */
    public static List<MenuConfig> getAll() {
        return Arrays.stream(values())
                .map(item -> new MenuConfig(item.key, item.name, item.shortName, item.sort))
                .collect(Collectors.toList());
    }

    @Data
    public static class MenuConfig {
        private final String key;
        private final String name;
        private final String shortName;
        private final Integer sort;
    }
}