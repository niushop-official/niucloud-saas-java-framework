package com.niu.core.service.api.member.vo;

import lombok.Data;

@Data
public class MemberAddressListInfoVo {

    // 会员地址ID
    private Integer id;

    // 会员id
    private Integer memberId;

    // 站点id
    private Integer siteId;

    // 用户姓名
    private String name;

    // 手机
    private String mobile;

    // 省id
    private Integer provinceId;

    // 市id
    private Integer cityId;

    // 区县id
    private Integer districtId;

    // 地址信息
    private String address;

    // 地址信息
    private String addressName;

    // 详细地址信息
    private String fullAddress;

    // 经度
    private String lng;

    // 纬度
    private String lat;

    // 是否是默认地址
    private Integer isDefault;

    // 地址类型  address 普通地址  location_address 定位地址
    private String type;

}
