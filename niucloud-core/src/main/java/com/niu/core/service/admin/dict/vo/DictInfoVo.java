package com.niu.core.service.admin.dict.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据字典
 */
@Data
public class DictInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer Id;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典关键字
     */
    private String key;

    /**
     * 字典json
     */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String dictionary;

    /**
     * 字典简介
     */
    private String memo;

    /**
     * 添加时间
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**
     * 最后修改时间
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

}