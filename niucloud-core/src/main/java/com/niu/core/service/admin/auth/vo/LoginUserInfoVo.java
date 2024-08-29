package com.niu.core.service.admin.auth.vo;

import com.niu.core.service.admin.sys.vo.SysUserInfoVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LoginUserInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer uid;
    private String username;
    private Boolean isSuperAdmin;
    private String headImg;
    private List<Integer> siteIds;
}
