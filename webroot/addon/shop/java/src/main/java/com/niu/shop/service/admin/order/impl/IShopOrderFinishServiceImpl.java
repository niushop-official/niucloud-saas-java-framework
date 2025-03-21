package com.niu.shop.service.admin.order.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.enums.order.OrderLogMainTypeEnum;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.admin.order.IShopOrderFinishService;
import com.niu.shop.service.core.order.ICoreOrderFinishService;
import com.niu.shop.service.core.order.param.OrderFinishParam;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class IShopOrderFinishServiceImpl implements IShopOrderFinishService {

    @Resource
    ICoreOrderFinishService coreOrderFinishService;

    @Override
    public void finish(Integer orderId) {
        OrderFinishParam finishParam = new OrderFinishParam();
        finishParam.setOrderId(orderId);
        finishParam.setSiteId(RequestUtils.siteId());
        finishParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        finishParam.setMainId(RequestUtils.uid());
        coreOrderFinishService.finish(finishParam);

    }
}
