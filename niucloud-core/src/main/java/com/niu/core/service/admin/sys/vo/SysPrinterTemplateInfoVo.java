package com.niu.core.service.admin.sys.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 小票打印模板详情
 */
@Data
public class SysPrinterTemplateInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 创建时间
     */
    private String createTime;


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
    private JSONObject value;

}