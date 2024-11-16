package com.niu.core.enums.sys;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CacheTagEnum {
    SITE_CASH("站点缓存", "site_cache"),
    USER_ROLE_CACHE("用户组缓存", "user_role_cache"),
    SYS_CONFIG_CACHE("配置缓存", "sys_config_cache"),
    MENU_CACHE("菜单缓存", "menu_cache");

    private final String name;

    private final String tagName;
}
