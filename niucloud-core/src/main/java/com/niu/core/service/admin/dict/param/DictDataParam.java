package com.niu.core.service.admin.dict.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 字典数据参数
*/
@Data
public class DictDataParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "typeId参数缺失")
    private Integer typeId;

    @NotNull(message = "name参数缺失")
    private String name;

    @NotNull(message = "value参数缺失")
    private String value;

    @NotNull(message = "remark参数缺失")
    private String remark;

    @NotNull(message = "sort参数缺失")
    private Integer sort;

    @NotNull(message = "status参数缺失")
    private Integer status;

}
