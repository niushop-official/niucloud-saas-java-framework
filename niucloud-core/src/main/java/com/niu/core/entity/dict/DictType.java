package com.niu.core.entity.dict;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典类型实体
 */
@Data
public class DictType implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 字典名称 */
    private String dictName;

    /** 字典类型 */
    private String dictType;

    /** 字典备注 */
    private String dictRemark;

    /** 字典状态: 0=停用, 1=正常 */
    private Integer dictStatus;

    /** 是否删除: 0=否, 1=是 */
    private Integer isDelete;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

    /** 删除时间 */
    private Long deleteTime;

}