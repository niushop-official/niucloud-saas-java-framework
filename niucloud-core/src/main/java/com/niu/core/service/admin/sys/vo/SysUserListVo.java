package com.niu.core.service.admin.sys.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * SysUserVo
 */
@Data
public class SysUserListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**系统用户ID */
    private Integer uid;

    /**用户账号 */
    private String username;

    /**用户头像 */
    private String headImg;

    /**用户密码 */
    private String password;

    /**实际姓名 */
    private String realName;

    /**最后一次登录ip */
    private String lastIp;

    /**最后一次登录时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private long lastTime;

    /**添加时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private long createTime;

    /**登录次数 */
    private Integer loginCount;

    /**用户状态 */
    private Integer status;

    /**是否删除 */
    private Integer isDel;

    /**更新时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private long updateTime;

    /**站点数量 */
    private Integer siteNum;

    /**是否管理员 */
    private Boolean isSuperAdmin;

}
