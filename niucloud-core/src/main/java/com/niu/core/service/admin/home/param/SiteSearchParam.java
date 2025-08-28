package com.niu.core.service.admin.home.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 站点参数
 */
@Data
public class SiteSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keywords;

    private Integer status;

    private Integer groupId;

    private String[] createTime;

    private String[] expireTime;

    private String app;

    private String siteDomain;

    private String appType;

    private String sort;

}
