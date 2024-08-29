package com.niu.core.service.admin.home.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.addon.Addon;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Site视图
 */
@Data
public class SiteInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;  // 主键
    private String siteName;  // 站点名称
    private Integer groupId;  // 分组ID(0:不限制)
    private String groupName; //分组名称
    private String keywords;  // 关键字
    private String appType;  // 站点类型
    private String logo;  // 站点logo
    private String desc;  // 简介
    private Integer status;  // 状态 1-正常 0-体验期 2-已到期
    private String latitude;  // 纬度
    private String longitude;  // 经度
    private Integer provinceId;  // 省
    private Integer cityId;  // 市
    private Integer districtId;  // 区
    private String address;  // 详细地址
    private String fullAddress;  // 完整地址
    private String phone;  // 客服电话
    private String businessHours;  // 营业时间
    private Long expireTime;  // 到期时间（如果是0 无限期）
    private String frontEndName;  // 前台名称
    private String frontEndLogo;  // 前台logo
    private String frontEndIcon;  // 前台icon
    private String icon;  // 网站图标
    private String memberNo;  // 最大会员码值
     @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String app;  // 站点应用
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String addons;  // 站点包含的插件
    private List<Addon> siteAddons;
    private List<Addon> apps;
    private List<String> addonKeys;
    private String initalledAddon;  // 站点已执行初始化方法的插件
    private String siteDomain;  // 站点域名


}
