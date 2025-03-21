package com.niu.core.service.admin.dict.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 字典数据参数
*/
@Data
public class DictDataParam implements Serializable {

    private static final long serialVersionUID = 1L;


    @NotNull(message = "字典数据不能为空")
    private String dictionary;


}
