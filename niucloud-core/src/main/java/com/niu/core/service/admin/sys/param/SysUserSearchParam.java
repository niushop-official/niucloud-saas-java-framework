package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 后台管理员参数
 */
@Data
public class SysUserSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户名称 */
    private String username;

    /** 站点名称 */
    private String siteName;

    /** 最后登录时间 */
    private String[] lastTime;

}
