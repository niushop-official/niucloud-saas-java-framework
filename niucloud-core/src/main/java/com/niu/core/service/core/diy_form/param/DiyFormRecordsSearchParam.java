package com.niu.core.service.core.diy_form.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 万能表单填写记录参数
 */
@Data
public class DiyFormRecordsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 站点id */
    private Integer siteId;

    /** 所属万能表单id */
    private Integer formId;

    private String keyword;



}
