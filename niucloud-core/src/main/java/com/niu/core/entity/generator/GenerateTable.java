package com.niu.core.entity.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 代码生成表
 */
@Data
public class GenerateTable implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String tableName;
    private String tableContent;
    private String moduleName;
    private String className;
    private Long createTime;
    private Integer editType;
    private String addonName;
    private Integer orderType;
    private String parentMenu;
    private String relations;
    private Integer synchronousNumber;

}