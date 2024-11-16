package com.niu.shop.service.admin.delivery.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 配送员参数
 */
@Data
public class ShopDeliveryDeliverSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String deliverName;

    private String deliverMobile;

}
