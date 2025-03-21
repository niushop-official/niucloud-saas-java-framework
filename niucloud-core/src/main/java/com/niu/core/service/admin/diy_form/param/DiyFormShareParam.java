package com.niu.core.service.admin.diy_form.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 万能表单表参数
*/
@Data
public class DiyFormShareParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 表单id */
    private Integer formId;

    private String share;


}
