package com.niu.core.entity.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 代码生成表字段信息表
 */
@Data
public class GenerateColumn implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer tableId;
    private String columnName;
    private String columnComment;
    private String columnType;
    private Integer isRequired;
    private Integer isPk;
    private Integer isInsert;
    private Integer isUpdate;
    private Integer isLists;
    private Integer isQuery;
    private Integer isSearch;
    private String queryType;
    private String viewType;
    private String dictType;
    private String addon;
    private String model;
    private String labelKey;
    private String valueKey;
    private Long createTime;
    private Long updateTime;
    private Integer isDelete;
    private Integer isOrder;
    private String validateType;

}