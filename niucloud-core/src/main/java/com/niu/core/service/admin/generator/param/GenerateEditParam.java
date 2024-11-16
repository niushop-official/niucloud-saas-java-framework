package com.niu.core.service.admin.generator.param;

import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 代码生成创建
 */
@Data
public class GenerateEditParam implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private Integer editType = 1;

    /**插件名*/
    private String addonName;

    /**排序方式 0-无排序 1-正序 2-倒序*/
    private Integer orderType = 0;

    private String orderColumnName = "";

    /**上级菜单*/
    private String parentMenu = "";

    /**关联配置*/
    private String relations = "";

    /**同步次数*/
    private Integer synchronousNumber;

    private Integer isDelete = 0;

    private String deleteColumnName = "";



    private String tableColumn;

}
