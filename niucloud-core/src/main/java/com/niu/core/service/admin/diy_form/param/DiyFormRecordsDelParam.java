package com.niu.core.service.admin.diy_form.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 万能表单填写记录参数
*/
@Data
public class DiyFormRecordsDelParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 表单填写记录id */
    private Integer recordId;


    /** 所属万能表单id */
    private Integer formId;


}
