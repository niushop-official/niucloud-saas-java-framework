package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员操作记录参数
 */
@Data
public class SysUserLogSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ip;

    private Integer siteId;

    private Integer uid;

    private String username;

    private String url;

    private String params;

    private String type;

}
