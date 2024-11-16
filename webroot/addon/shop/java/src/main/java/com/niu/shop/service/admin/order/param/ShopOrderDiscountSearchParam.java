package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单优惠参数
 */
@Data
public class ShopOrderDiscountSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer orderId;

    private String orderGoodsIds;

    private String type;

    private Integer num;

    private BigDecimal money;

    private String discountType;

    private Integer discountTypeId;

    private String content;

    private Integer status;

}
