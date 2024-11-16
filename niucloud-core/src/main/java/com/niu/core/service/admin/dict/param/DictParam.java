package com.niu.core.service.admin.dict.param;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 字典数据参数
*/
@Data
public class DictParam implements Serializable {

    private static final long serialVersionUID = 1L;


    @NotNull(message = "字典名称不能为空")
    private String name;

    @NotNull(message = "字典关键字不能为空")
    private String key;

    private String dictionary;

    /**
     * 字典简介
     */
    private String memo = "";

}
