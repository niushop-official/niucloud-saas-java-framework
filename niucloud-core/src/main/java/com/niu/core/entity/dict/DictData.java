package com.niu.core.entity.dict;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典数据实体
 */
@Data
public class DictData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // 主键

    /** 类型 */
    private Integer typeId;

    /** 键名 */
    private String name;

    /** 数值 */
    private String value;

    /** 备注 */
    private String remark;

    /** 排序 */
    private Integer sort;

    /** 状态: 0=停用, 1=正常 */
    private Integer status;

    /** 是否删除: 0=否, 1=是 */
    private Integer isDelete;

    /** 创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

    /** 删除时间 */
    private Long deleteTime;

}