package com.niu.core.service.admin.site.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.mapper.sys.SysRoleMapper;
import lombok.Data;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

@Data
public class SiteUserVo implements Serializable {


    private static final long serialVersionUID = 1L;

    /** 创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**头像 */
    private String headImg;

    /**主键id */
    private Integer id;

    /**是否是管理员 */
    private Integer isAdmin;

    /**最后登录ip */
    private String lastIp;

    /**最后登录时间（如果是0 无限期） */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long lastTime;

    /**登录次数 */
    private Integer loginCount;

    /**真实名称 */
    private String realName;

    /**角色id */
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String roleIds;

    /**角色集合 */
    private List<String> roleArray;

    /**站点id */
    private Integer siteId;

    /**状态 */
    private Integer status;

    /**状态名称 */
    private String statusName;

    /**用户id */
    private Integer uid;

    /**用户名 */
    private String username;


    public String getStatusName() {
        return StatusEnum.getNameByStatus(this.status);
    }
}
