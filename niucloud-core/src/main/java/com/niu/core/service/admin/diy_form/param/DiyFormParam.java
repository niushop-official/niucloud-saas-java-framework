package com.niu.core.service.admin.diy_form.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 万能表单表参数
*/
@Data
public class DiyFormParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 表单id */
    private Integer formId;

    /** 站点id */
    private Integer siteId;

    /** 表单名称（用于后台展示） */
    private String pageTitle;

    /** 表单名称（用于前台展示） */
    private String title;

    /** 表单类型 */
    private String type;


    /** 模板名称 */
    private String template;

    /** 表单数据，json格式，包含展示组件 */
    private String value;

    /** 所属插件标识 */
    private String addon;

    /** 分享内容 */
    private String share;

    /** 备注说明 */
    private String remark;


}
