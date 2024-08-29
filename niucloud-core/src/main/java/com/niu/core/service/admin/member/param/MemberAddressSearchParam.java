package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员收货地址参数
 */
@Data
public class MemberAddressSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;

    private Integer siteId;

    private String name;

    private String mobile;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    private String address;

    private String addressName;

    private String fullAddress;

    private String lng;

    private String lat;

    private Integer isDefault;

    private String type;

}
