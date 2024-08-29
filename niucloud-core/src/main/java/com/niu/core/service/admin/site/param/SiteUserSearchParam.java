package com.niu.core.service.admin.site.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户管理员请求类
 */
@Data
public class SiteUserSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String username;
    /**
     * 用户类型
     */
    private String userType;

}
