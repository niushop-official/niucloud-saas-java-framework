package com.niu.core.service.admin.member.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员收货地址参数
 */
@Data
public class MemberAddressParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "缺少必须参数member_id")
    private Integer memberId;

    @NotNull(message = "缺少必须参数name")
    private String name;

    @NotNull(message = "缺少必须参数mobile")
    private String mobile;

    @NotNull(message = "缺少必须参数province_id")
    private Integer provinceId;

    @NotNull(message = "缺少必须参数city_id")
    private Integer cityId;

    @NotNull(message = "缺少必须参数district_id")
    private Integer districtId;

    @NotNull(message = "缺少必须参数address")
    private String address;

    private String addressName;

    @NotNull(message = "缺少必须参数full_address")
    private String fullAddress;

    private String lng = "";

    private String lat = "";

    private Integer isDefault = 0;

}
