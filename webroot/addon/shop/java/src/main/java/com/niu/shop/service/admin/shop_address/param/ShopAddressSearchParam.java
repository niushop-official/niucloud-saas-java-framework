package com.niu.shop.service.admin.shop_address.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商家地址库参数
 */
@Data
public class ShopAddressSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mobile;

    private String fullAddress;

}
