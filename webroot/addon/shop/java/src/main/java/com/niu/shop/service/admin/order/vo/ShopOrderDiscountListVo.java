package com.niu.shop.service.admin.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopOrderDiscount列表视图
 */
@Data
public class ShopOrderDiscountListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private Integer orderId;  // 订单id
    private String orderGoodsIds;  // 参与的订单商品项
    private String type;  // 类型 discount 优惠，gift 赠送
    private Integer num;  // 使用数量
    private BigDecimal money;  // 优惠金额
    private String discountType;  // 优惠类型
    private Integer discountTypeId;  // 优惠类型id
    private String createTime; // 创建时间
    private Integer status;  // 状态

}
