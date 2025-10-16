package com.niu.core.service.core.user.dto;

import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import lombok.Data;

@Data
public class UserInfoDto {
    private Integer uid;

    /** 用户账号 */
    private String username;

    /** 用户头像  */
    private String headImg;

    /** 用户密码 */
    private String password;

    /** 实际姓名 */
    private String realName;

    /** 最后一次登录ip */
    private String lastIp;

    /** 最后一次登录时间 */
    private Long lastTime;

    /** 添加时间 */
    private Long createTime;

    /** 登录次数 */
    private Integer loginCount;

    /** 状态 */
    private Integer status;

    /** 是否删除 */
    private Integer isDel;

    /** 删除时间 */
    private Long deleteTime;

    /** 更新时间 */
    private Long updateTime;

    private SysUserRoleInfoVo role;
}
