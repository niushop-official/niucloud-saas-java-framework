package com.niu.core.service.admin.dict.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * DictDataVo
 */
@Data
public class DictDataInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private Integer typeId;  // 类型
    private String name;  // 键名
    private String value;  // 数值
    private String remark;  // 备注
    private Integer sort;  // 排序
    private Integer status;  // 状态: 0=停用, 1=正常

}
