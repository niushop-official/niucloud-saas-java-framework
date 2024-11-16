package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单项优惠参数
 */
@Data
public class ShopOrderDiscountGoodsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer orderDiscountId;

    private Integer orderId;

    private String orderGoodsId;

    private String type;

    private Integer num;

    private BigDecimal money;

}
