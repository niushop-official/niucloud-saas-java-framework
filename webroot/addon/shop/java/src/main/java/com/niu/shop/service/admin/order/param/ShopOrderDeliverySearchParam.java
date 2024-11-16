package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单发货参数
 */
@Data
public class ShopOrderDeliverySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer orderId;

    private String name;

    private String deliveryType;

    private String subDeliveryType;

    private Integer expressCompanyId;

    private String expressNumber;

    private Integer localDeliverId;

    private Integer status;

    private String remark;

}
