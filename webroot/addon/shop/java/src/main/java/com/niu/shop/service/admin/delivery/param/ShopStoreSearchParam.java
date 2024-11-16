package com.niu.shop.service.admin.delivery.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 自提门店参数
 */
@Data
public class ShopStoreSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String storeName;

}
