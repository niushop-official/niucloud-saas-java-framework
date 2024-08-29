package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单实体
 */
@Data
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id; //

    /** 应用类型 */
    private String appType;

    /** 菜单名称 */
    private String menuName;

    /** 菜单短标题 */
    private String menuShortName;

    /** 菜单标识（菜单输入，接口自动生成） */
    private String menuKey;

    /** 父级key */
    private String parentKey;

    /** 菜单类型 0目录 1菜单 2按钮 */
    private Integer menuType;

    /** 图标 菜单有效 */
    private String icon;

    /** api接口地址 */
    private String apiUrl;

    /** 菜单路由地址 前端使用 */
    private String routerPath;

    /** 菜单文件地址 */
    private String viewPath;

    /** 提交方式POST GET PUT DELETE */
    private String methods;

    /** 排序 */
    private Integer sort;

    /** 正常，禁用（禁用后不允许访问） */
    private Integer status;

    /** 是否显示 */
    private Integer isShow;

    private Long createTime;

    private Long deleteTime;

    /** 所属插件 */
    private String addon;

    /** 菜单来源   system 系统文件  create 新建菜单  generator 代码生成器 */
    private String source;

    /** 菜单属性 common 公共 system 系统 */
    private String menuAttr;

}