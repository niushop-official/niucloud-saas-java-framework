package com.niu.core.service.admin.diy_form.param;

import lombok.Data;
import java.io.Serializable;

/**
 * 万能表单表参数
 */
@Data
public class DiyFormSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 表单id */
    private Integer formId;

    /** 表单名称（用于前台展示） */
    private String title;

    /** 表单类型 */
    private String type;

    /** 所属插件标识 */
    private String addon;

    private Integer status;
}
