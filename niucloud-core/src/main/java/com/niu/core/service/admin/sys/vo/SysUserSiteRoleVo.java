package com.niu.core.service.admin.sys.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.UserEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户站点信息
 */
@Data
public class SysUserSiteRoleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**站点id */
    private Integer siteId;

    /**站点名称 */
    private String siteName;

    /**站点状态 */
    private Integer siteStatus;

    /**站点状态名称 */
    private String siteStatusName;

    /**站点状态 */
    private Integer status;

    /**状态名称 */
    private String statusName;

    /**用户id */
    private Integer uid;

    /**是否是管理员 */
    private Integer isAdmin;

    /**id */
    private Integer id;

    /**过期时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long expireTime;

    /**创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**类型 */
    private String appType;

    /**角色id */
    private List<String> roleIds;

    public String getSiteName() {
        return UserEnum.getNameByCode(this.status);
    }

    public String getSiteStatusName() {
        return SiteStatusEnum.getNameByCode(this.siteStatus);
    }
}
