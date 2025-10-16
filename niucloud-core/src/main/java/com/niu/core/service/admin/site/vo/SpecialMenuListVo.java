package com.niu.core.service.admin.site.vo;

import lombok.Data;

import java.util.List;

@Data
public class SpecialMenuListVo {
    private String parentKey;

    private List<MenuVo> list;

    public SpecialMenuListVo() {}

    public SpecialMenuListVo(String parentKey, List<MenuVo> list) {
        this.parentKey = parentKey;
        this.list = list;
    }

    @Data
    public static class MenuVo {
        private String key;
        private String menuName;
        private String menuKey;
        private String menuShortName;
        private String parentKey;
        private String menuType;
        private String icon;
        private String apiUrl;
        private String routerPath;
        private String viewPath;
        private String methods;
        private Integer sort;
        private String status;
        private String isShow;
        private List<MenuVo> children;}
}
