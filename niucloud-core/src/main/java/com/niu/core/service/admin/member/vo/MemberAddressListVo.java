package com.niu.core.service.admin.member.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * MemberAddress列表视图
 */
@Data
public class MemberAddressListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer memberId;  // 会员id
    private Integer siteId;  // 站点id
    private String name;  // 用户姓名
    private String mobile;  // 手机
    private Integer provinceId;  // 省id
    private Integer cityId;  // 市id
    private Integer districtId;  // 区县id
    private String address;  // 地址信息
    private String addressName;  // 
    private String fullAddress;  // 详细地址信息
    private String lng;  // 经度
    private String lat;  // 纬度
    private Integer isDefault;  // 是否是默认地址
    private String type;  // 地址类型  address 普通地址  location_address 定位地址

}
