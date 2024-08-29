package com.niu.core.service.admin.sys.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.member.StatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * SysRoleVo
 */
@Data
public class SysRoleListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**角色id */
    private Integer roleId;

    /**站点id */
    private Integer siteId;

    /**角色名称 */
    private String roleName;

    /**角色权限(menus_id) */
    private String rules;

    /**状态 */
    private Integer status;

    /**添加时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**最后修改时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    /**状态名称 */
    private String statusName;

    public String getStatusName() {
        return StatusEnum.getNameByStatus(this.status);
    }
}
