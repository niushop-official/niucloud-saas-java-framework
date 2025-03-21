package com.niu.shop.service.admin.shop_address.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 商家地址库参数
*/
@Data
public class ShopAddressParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "contact_name参数缺失")
    @NotEmpty(message = "请输入联系人")
    private String contactName;

    @NotNull(message = "mobile参数缺失")
    @NotEmpty(message = "请输入联系方式")
    private String mobile;

    @NotNull(message = "province_id参数缺失")
    @Min(value = 1, message = "请选择地址")
    private Integer provinceId;

    @NotNull(message = "city_id参数缺失")
    @Min(value = 1, message = "请选择地址")
    private Integer cityId;

    @NotNull(message = "district_id参数缺失")
    @Min(value = 1, message = "请选择地址")
    private Integer districtId;

    @NotNull(message = "address参数缺失")
    @NotEmpty(message = "请输入详细地址")
    private String address;

    @NotNull(message = "full_address参数缺失")
    private String fullAddress;

    @NotNull(message = "lat参数缺失")
    private String lat;

    @NotNull(message = "lng参数缺失")
    private String lng;

    private Integer isDeliveryAddress = 0;

    private Integer isRefundAddress = 0;

    private Integer isDefaultDelivery = 0;

    private Integer isDefaultRefund = 0;

}
