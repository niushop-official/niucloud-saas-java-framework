package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 会员收货地址参数
*/
@Data
public class MemberAddressParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "memberId参数缺失")
    private Integer memberId;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "name参数缺失")
    private String name;

    @NotNull(message = "mobile参数缺失")
    private String mobile;

    @NotNull(message = "provinceId参数缺失")
    private Integer provinceId;

    @NotNull(message = "cityId参数缺失")
    private Integer cityId;

    @NotNull(message = "districtId参数缺失")
    private Integer districtId;

    @NotNull(message = "address参数缺失")
    private String address;

    @NotNull(message = "addressName参数缺失")
    private String addressName;

    @NotNull(message = "fullAddress参数缺失")
    private String fullAddress;

    @NotNull(message = "lng参数缺失")
    private String lng;

    @NotNull(message = "lat参数缺失")
    private String lat;

    @NotNull(message = "isDefault参数缺失")
    private Integer isDefault;

    @NotNull(message = "type参数缺失")
    private String type;

}
