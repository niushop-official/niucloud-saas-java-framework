package com.niu.core.service.admin.site.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.site.SiteStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * Site列表视图
 */
@Data
public class SiteAdminVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer uid;  // 系统用户ID
    private String username;  // 用户账号
    private String headImg;  //
    private String realName;  // 实际姓名
    private String lastIp;  // 最后一次登录ip
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long lastTime;  // 最后一次登录时间
    private Integer loginCount;  // 登录次数


}
