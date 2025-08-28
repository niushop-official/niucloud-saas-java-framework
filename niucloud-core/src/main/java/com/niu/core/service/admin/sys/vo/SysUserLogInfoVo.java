package com.niu.core.service.admin.sys.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * SysUserLogVo
 */
@Data
public class SysUserLogInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 管理员操作记录ID
    private String ip;  // 登录IP
    private Integer siteId;  // 站点id
    private Integer uid;  // 管理员id
    private String username;  // 管理员姓名
    private String url;  // 链接
    private String params;  // 参数
    private String type;  // 请求方式
    private String operation;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 操作时间

}
