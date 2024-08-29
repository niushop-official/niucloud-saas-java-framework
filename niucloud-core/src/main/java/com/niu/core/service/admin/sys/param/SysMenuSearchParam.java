package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜单参数
 */
@Data
public class SysMenuSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appType;

    private String menuName;

    private String menuShortName;

    private String menuKey;

    private String parentKey;

    private Integer menuType;

    private String icon;

    private String apiUrl;

    private String routerPath;

    private String viewPath;

    private String methods;

    private Integer sort;

    private Integer status;

    private Integer isShow;

    private String addon;

    private String source;

    private String menuAttr;

}
