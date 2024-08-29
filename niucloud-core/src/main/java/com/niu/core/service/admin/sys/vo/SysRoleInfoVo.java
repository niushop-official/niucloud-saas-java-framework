package com.niu.core.service.admin.sys.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * SysRoleVo
 */
@Data
public class SysRoleInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**角色id */
    private Integer roleId;

    /**站点id */
    private Integer siteId;

    /**角色名称 */
    private String roleName;

    /**角色管线 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String rules;

    /**状态 */
    private Integer status;

}
