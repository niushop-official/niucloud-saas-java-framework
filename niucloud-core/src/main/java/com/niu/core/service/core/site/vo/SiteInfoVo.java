package com.niu.core.service.core.site.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SiteInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer groupId; // 分组ID(0:不限制)
    private String appType; // 站点类型
    private String addons; // 站点包含的插件


}
