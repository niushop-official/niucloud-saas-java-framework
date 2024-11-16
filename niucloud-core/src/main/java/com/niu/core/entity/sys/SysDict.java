package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据字典
 */
@Data
public class SysDict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer Id;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典关键字
     */
    @TableField(value = "`key`")
    private String key;

    /**
     * 字典json
     */
    private String dictionary;

    /**
     * 字典简介
     */
    @TableField(value = "`memo`")
    private String memo;

    /**
     * 添加时间
     */
    private Long createTime;

    /**
     * 最后修改时间
     */
    private Long updateTime;

}