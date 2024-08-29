package com.niu.core.service.admin.home.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 站点参数
*/
@Data
public class SiteParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "siteName参数缺失")
    private String siteName;

    @NotNull(message = "groupId参数缺失")
    private Integer groupId;

    @NotNull(message = "keywords参数缺失")
    private String keywords;

    @NotNull(message = "appType参数缺失")
    private String appType;

    @NotNull(message = "logo参数缺失")
    private String logo;

    @NotNull(message = "desc参数缺失")
    private String desc;

    @NotNull(message = "status参数缺失")
    private Integer status;

    @NotNull(message = "latitude参数缺失")
    private String latitude;

    @NotNull(message = "longitude参数缺失")
    private String longitude;

    @NotNull(message = "provinceId参数缺失")
    private Integer provinceId;

    @NotNull(message = "cityId参数缺失")
    private Integer cityId;

    @NotNull(message = "districtId参数缺失")
    private Integer districtId;

    @NotNull(message = "address参数缺失")
    private String address;

    @NotNull(message = "fullAddress参数缺失")
    private String fullAddress;

    @NotNull(message = "phone参数缺失")
    private String phone;

    @NotNull(message = "businessHours参数缺失")
    private String businessHours;

    @NotNull(message = "expireTime参数缺失")
    private Long expireTime;

    @NotNull(message = "frontEndName参数缺失")
    private String frontEndName;

    @NotNull(message = "frontEndLogo参数缺失")
    private String frontEndLogo;

    @NotNull(message = "frontEndIcon参数缺失")
    private String frontEndIcon;

    @NotNull(message = "icon参数缺失")
    private String icon;

    @NotNull(message = "memberNo参数缺失")
    private Integer memberNo;

    @NotNull(message = "app参数缺失")
    private String app;

    @NotNull(message = "addons参数缺失")
    private String addons;

    @NotNull(message = "initalledAddon参数缺失")
    private String initalledAddon;

    @NotNull(message = "siteDomain参数缺失")
    private String siteDomain;

}
