package com.niu.shop.service.core.order;

import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import com.niu.shop.service.core.order.vo.OrderCouponListVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;

import javax.annotation.Resource;
import java.util.List;

public interface ICoreOrderCreateService {

    /**
     * 计算
     *
     * @param param
     */
    OrderCreateDataVo calculate(OrderCalculateParam param);

    /**
     * 创建
     *
     * @param param
     * @return
     */
    OrderCreateResultVo create(OrderCreateParam param);

    /**
     * 查询订单可用优惠券
     * @param orderKey
     */
    List<OrderCouponListVo> getCoupon(String orderKey);
}
