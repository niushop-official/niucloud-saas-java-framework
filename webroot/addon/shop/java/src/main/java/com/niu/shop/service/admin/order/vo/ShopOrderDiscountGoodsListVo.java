package com.niu.shop.service.admin.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopOrderDiscountGoods列表视图
 */
@Data
public class ShopOrderDiscountGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private Integer orderDiscountId;  // 订单优惠id
    private Integer orderId;  // 订单id
    private String orderGoodsId;  // 参与的订单商品项
    private String type;  // 类型 discount 优惠，gift 赠送
    private Integer num;  // 使用数量
    private BigDecimal money;  // 优惠金额

}
