package com.niu.shop.service.core.order.param;

import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单发货参数
 */
@Data
public class OrderDeliveryDataParam implements Serializable {
    private static final long serialVersionUID = 1L;

    ShopOrder orderData;

    List<ShopOrderGoods> orderGoodsData;

    OrderDeliveryParam param;

}
