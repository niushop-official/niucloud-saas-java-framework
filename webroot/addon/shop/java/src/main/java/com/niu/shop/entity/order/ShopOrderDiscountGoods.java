package com.niu.shop.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单项优惠实体
 */
@Data
public class ShopOrderDiscountGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // 
    private Integer siteId; // 站点id
    private Integer orderDiscountId; // 订单优惠id
    private Integer orderId; // 订单id
    private String orderGoodsId; // 参与的订单商品项
    private String type; // 类型 discount 优惠，gift 赠送
    private Integer num; // 使用数量
    private BigDecimal money; // 优惠金额

}