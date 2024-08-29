package com.niu.core.service.admin.diy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * DiyRouteVo
 */
@Data
public class DiyRouteInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private String title;  // 页面名称
    private String name;  // 页面标识
    private String page;  // 页面路径
    private String share;  // 分享内容
    private Integer isShare;  // 是否支持分享
    private Integer sort;  // 排序

}
