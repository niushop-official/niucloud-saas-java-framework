package com.niu.shop.service.api.order;

import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryInfoVo;
import com.niu.shop.service.core.delivery.param.LatlngParam;
import com.niu.shop.service.core.delivery.vo.StoreListVo;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import com.niu.shop.service.core.order.vo.OrderCouponListVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;

import java.util.List;

public interface IOrderCreateService {

    /**
     * 计算
     *
     * @param param
     */
    OrderCreateDataVo calculate(OrderCalculateParam param);

    /**
     * 订单创建
     *
     * @param param
     * @return
     */
    OrderCreateResultVo create(OrderCreateParam param);

    /**
     * 获取优惠券列表
     *
     * @param orderKey
     */
    List<OrderCouponListVo> getCoupon(String orderKey);

    /**
     * 获取自提点列表
     *
     * @param latlng
     * @return
     */
    List<StoreListVo> getStore(LatlngParam latlng);

    /**
     * 获取本地配送数据
     * @return
     */
    ShopDeliveryLocalDeliveryInfoVo getLocal();
}
