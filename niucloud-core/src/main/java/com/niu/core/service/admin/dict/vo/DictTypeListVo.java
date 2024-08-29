package com.niu.core.service.admin.dict.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * DictTypeVo
 */
@Data
public class DictTypeListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private String dictName;  // 字典名称
    private String dictType;  // 字典类型
    private String dictRemark;  // 字典备注
    private Integer dictStatus;  // 字典状态: 0=停用, 1=正常
    private String createTime; // 创建时间
    private String updateTime; // 更新时间

}
