package com.niu.core.service.admin.dict.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典数据参数
 */
@Data
public class DictDataSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer typeId;

    private String name;

    private String value;

    private String remark;

    private Integer sort;

    private Integer status;

}
