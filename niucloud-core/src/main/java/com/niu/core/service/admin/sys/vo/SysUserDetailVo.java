package com.niu.core.service.admin.sys.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户详情Vo
 */
@Data
public class SysUserDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**系统用户id */
    private Integer uid;

    /**用户账号 */
    private String username;

    /**头像 */
    private String headImg;

    /**用户名 */
    private String password;

    /**实际姓名 */
    private String realName;

    /**最后一次登录IP */
    private String lastIp;

    /**登录次数 */
    private Integer loginCount;

    /**最后一次登录时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private long lastTime;

    /**添加时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private long createTime;

    /**是否管理员 */
    private Boolean isSuperAdmin;

    /**站点信息 */
    private List<SysUserSiteRoleVo> roles;
}
