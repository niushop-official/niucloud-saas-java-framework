package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * SysMenuVo
 */
@Data
public class SysMenuListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 菜单ID
    private String appType;  // 应用类型
    private String menuName;  // 菜单名称
    private String menuShortName;  // 菜单短标题
    private String menuKey;  // 菜单标识（菜单输入，接口自动生成）
    private String parentKey;  // 父级key
    private Integer menuType;  // 菜单类型 0目录 1菜单 2按钮
    private String icon;  // 图标 菜单有效
    private String apiUrl;  // api接口地址
    private String routerPath;  // 菜单路由地址 前端使用
    private String viewPath;  // 菜单文件地址
    private String methods;  // 提交方式POST GET PUT DELETE
    private Integer sort;  // 排序
    private Integer status;  // 正常，禁用（禁用后不允许访问）
    private Integer isShow;  // 是否显示
    private String createTime; // 
    private String addon;  // 所属插件
    private String source;  // 菜单来源   system 系统文件  create 新建菜单  generator 代码生成器
    private String menuAttr;  // 菜单属性 common 公共 system 系统

}
