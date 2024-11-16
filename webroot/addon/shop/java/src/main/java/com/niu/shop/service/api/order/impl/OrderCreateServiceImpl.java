package com.niu.shop.service.api.order.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.service.api.order.IOrderCreateService;
import com.niu.shop.service.core.delivery.ICoreStoreService;
import com.niu.shop.service.core.delivery.param.LatlngParam;
import com.niu.shop.service.core.delivery.vo.StoreListVo;
import com.niu.shop.service.core.order.ICoreOrderCreateService;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import com.niu.shop.service.core.order.vo.OrderCouponListVo;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderCreateServiceImpl implements IOrderCreateService {

    @Resource
    ICoreOrderCreateService coreOrderCreateService;

    @Resource
    ICoreStoreService coreStoreService;

    /**
     * 计算
     *
     * @param param
     */
    @Override
    public OrderCreateDataVo calculate(OrderCalculateParam param) {
        param.setSiteId(RequestUtils.siteId());
        param.setMemberId(RequestUtils.memberId());
        param.setMemberId(RequestUtils.memberId());
        param.setOrderFrom(RequestUtils.channel());
        return coreOrderCreateService.calculate(param);
    }

    /**
     * 订单创建
     *
     * @param param
     * @return
     */
    @Override
    public OrderCreateResultVo create(OrderCreateParam param) {
        return coreOrderCreateService.create(param);
    }

    /**
     * 获取订单可用优惠券
     *
     * @param orderKey
     * @return
     */
    @Override
    public List<OrderCouponListVo> getCoupon(String orderKey) {
        return coreOrderCreateService.getCoupon(orderKey);
    }

    /**
     * 获取自提点列表
     *
     * @param latlng
     */
    @Override
    public List<StoreListVo> getStore(LatlngParam latlng) {
        return coreStoreService.getStoreList(RequestUtils.siteId(), latlng);
    }
}
