package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 小票打印模板实体
 */
@Data
public class SysPrinterTemplate implements Serializable {

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
    @TableId(value = "template_id", type = IdType.AUTO)
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