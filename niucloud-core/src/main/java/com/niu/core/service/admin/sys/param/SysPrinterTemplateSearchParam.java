package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 小票打印模板参数
 */
@Data
public class SysPrinterTemplateSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     *
     */
    private Integer templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 模板数据，json格式
     */
    private String value;


}
