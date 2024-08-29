package com.niu.core.service.admin.install.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单数据
 */
@Data
public class InstallMenuVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 菜单名称 */
    private String menuName;

    /** 菜单短名称 */
    private String menuShortName;

    /** 菜单key */
    private String menuKey;

    /** 端口 */
    private String appType;

    /** 上级菜单key */
    private String parentKey;

    /** 菜单类型 */
    private String menuType;

    /** 图标 */
    private String icon;

    /** api接口 */
    private String apiUrl;

    /** 路由地址 */
    private String routerPath;

    /** 页面路由 */
    private String viewPath;

    /** 请求类型 */
    private String methods;

    /** 排序 */
    private Integer sort;

    /** 状态 */
    private Integer status;

    /** 是否显示 */
    private Integer isShow;

    /**
     * 下级菜单
     */
    private List<InstallMenuVo> children;



}
