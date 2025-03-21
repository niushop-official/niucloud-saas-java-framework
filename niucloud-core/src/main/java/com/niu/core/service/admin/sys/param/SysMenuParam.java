package com.niu.core.service.admin.sys.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 菜单参数
*/
@Data
public class SysMenuParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "addon参数缺失")
    private String addon;

    @NotNull(message = "apiUrl参数缺失")
    private String apiUrl;

    @NotNull(message = "appType参数缺失")
    private String appType;

    @NotNull(message = "icon参数缺失")
    private String icon;

    @NotNull(message = "id参数缺失")
    private Integer id;

    @NotNull(message = "isShow参数缺失")
    private Integer isShow;

    @NotNull(message = "menuKey参数缺失")
    private String menuKey;

    @NotNull(message = "menuName参数缺失")
    private String menuName;

    @NotNull(message = "menuShortName参数缺失")
    private String menuShortName;

    @NotNull(message = "menuType参数缺失")
    private Integer menuType;

    @NotNull(message = "methods参数缺失")
    private String methods;

    @NotNull(message = "parentKey参数缺失")
    private String parentKey;

    @NotNull(message = "routerPath参数缺失")
    private String routerPath;

    @NotNull(message = "sort参数缺失")
    private Integer sort;

    @NotNull(message = "status参数缺失")
    private Integer status;

    @NotNull(message = "viewPath参数缺失")
    private String viewPath;

}
