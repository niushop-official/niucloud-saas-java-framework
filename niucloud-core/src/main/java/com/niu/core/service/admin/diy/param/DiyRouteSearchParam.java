package com.niu.core.service.admin.diy.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义路由参数
 */
@Data
public class DiyRouteSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String url;

    private String addonName;

}
