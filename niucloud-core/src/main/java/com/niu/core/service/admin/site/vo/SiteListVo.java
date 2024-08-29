package com.niu.core.service.admin.site.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * Site列表视图
 */
@Data
public class SiteListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer siteId;
    /** 站点名称 */
    private String siteName;
    /** 分组ID(0:不限制) */
    private Integer groupId;
    /** 站点套餐名称 */
    private String groupName;
    /** 关键字 */
    private String keywords;
    /** 站点类型 */
    private String appType;
    /** 站点logo */
    private String logo;
    /** 简介 */
    private String desc;
    /** 状态 1-正常 0-体验期 2-已到期 */
    private Integer status;
    /** 纬度 */
    private String latitude;
    /** 经度 */
    private String longitude;
    /** 省 */
    private Integer provinceId;
    /** 市 */
    private Integer cityId;
    /** 区 */
    private Integer districtId;
    /** 详细地址 */
    private String address;
    /** 完整地址 */
    private String fullAddress;
    /** 客服电话 */
    private String phone;
    /** 营业时间 */
    private String businessHours;
    /** 创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    /** 到期时间（如果是0 无限期） */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long expireTime;
    /** 前台名称*/
    private String frontEndName;
    /** 前台logo */
    private String frontEndLogo;
    /** 前台icon */
    private String frontEndIcon;
    /** 网站图标 */
    private String icon;
    /** 最大会员码值 */
    private String memberNo;
    /** 站点应用 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String app;
    /** 站点包含的插件 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String addons;
    /** 站点已执行初始化方法的插件 */
    private String initalledAddon;
    /** 站点域名 */
    private String siteDomain;
    /** 站点管理员 */
    private SiteAdminVo admin;
    /** 状态名称 */
    private String statusName;

    public String getStatusName() {
        return SiteStatusEnum.getNameByCode(this.status);
    }
}
