package com.niu.core.enums.sys;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CacheTagEnum {
    SITE_CACHE("站点缓存", "site_cache"),
    SITE_GROUP_CACHE("站点组缓存", "site_group_cache"),
    ADDON_CACHE("插件缓存", "addon_cache"),
    USER_ROLE_CACHE("用户组缓存", "user_role_cache"),
    SYS_CONFIG_CACHE("配置缓存", "sys_config_cache"),
    MENU_CACHE("菜单缓存", "menu_cache"),
    AREA_CACHE("地区缓存", "area_cache");

    private final String name;

    private final String tagName;
}
