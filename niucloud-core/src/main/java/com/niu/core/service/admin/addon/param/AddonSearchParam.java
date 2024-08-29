package com.niu.core.service.admin.addon.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 插件参数
 */
@Data
public class AddonSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String icon;

    private String key;

    private String desc;

    private Integer status;

    private String author;

    private String version;

    private Integer installTime;

    private String cover;

    private String type;

    private String supportApp;

    private Integer isStar;

    private String compile;

}
