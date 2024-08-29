package com.niu.core.service.admin.auth.vo;

import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token; // token
    private long expiresTime; // 失效时间
    private LoginUserInfoVo userinfo;
    private Integer siteId;
    private SiteInfoVo siteInfo;
    private SysUserRoleInfoVo userrole;



}
