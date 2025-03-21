package com.niu.shop.service.admin.delivery.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 自提门店参数
*/
@Data
public class ShopStoreParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "store_name参数缺失")
    @NotEmpty(message = "自提点名称不能为空")
    private String storeName;

    private String storeDesc = "";

    private String storeLogo = "";

    @NotNull(message = "store_mobile参数缺失")
    @NotEmpty(message = "自提点手机号不能为空")
    private String storeMobile;

    @NotNull(message = "province_name参数缺失")
    @NotEmpty(message = "请选择自提点地址")
    private String provinceName;

    @NotNull(message = "city_name参数缺失")
    @NotEmpty(message = "请选择自提点地址")
    private String cityName;

    @NotNull(message = "district_name参数缺失")
    @NotEmpty(message = "请选择自提点地址")
    private String districtName;

    @NotNull(message = "address参数缺失")
    @NotEmpty(message = "请填写自提点详细地址")
    private String address;

    private String fullAddress;

    @NotNull(message = "longitude参数缺失")
    @NotEmpty(message = "longitude不能为空")
    private String longitude;

    @NotNull(message = "latitude参数缺失")
    @NotEmpty(message = "latitude不能为空")
    private String latitude;

    @NotNull(message = "trade_time参数缺失")
    @NotEmpty(message = "营业时间不能为空")
    private String tradeTime;

}
