package com.niu.core.service.admin.generator.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TableListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**表名*/
    private String tableName;

    /**表描述*/
    private String tableContent;

    /**模块名*/
    private String moduleName;

    /**类名前缀*/
    private String className;

    /**添加时间*/
    private Long createTime;

    /**编辑方式1-弹框 2-新页面*/
    private Integer editType;

    /**插件名*/
    private String addonName;

    /**排序方式 0-无排序 1-正序 2-倒序*/
    private Integer orderType;

    /**上级菜单*/
    private String parentMenu;

    /**关联配置*/
    private String relations;

    /**同步次数*/
    private Integer synchronousNumber;


}
