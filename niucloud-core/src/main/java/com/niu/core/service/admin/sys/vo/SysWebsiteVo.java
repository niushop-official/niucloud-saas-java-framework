package com.niu.core.service.admin.sys.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.addon.Addon;
import lombok.Data;

import java.util.List;

@Data
public class SysWebsiteVo {
    private static final long serialVersionUID = 1L;

    private String siteName = "";  // 站点名称
    private String keywords = "";  // 关键字
    private String logo = "";  // 站点logo
    private String desc = "";  // 简介
    private String latitude = "";  // 纬度
    private String longitude = "";  // 经度
    private Integer provinceId = 0;  // 省
    private Integer cityId = 0;  // 市
    private Integer districtId = 0;  // 区
    private String address = "";  // 详细地址
    private String fullAddress = "";  // 完整地址
    private String phone = "";  // 客服电话
    private String businessHours = "";  // 营业时间
    private String frontEndName = "";  // 前台名称
    private String frontEndLogo = "";  // 前台logo
    private String frontEndIcon = "";  // 前台icon
    private String icon = "";  // 网站图标
}
