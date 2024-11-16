package com.niu.shop.service.admin.order.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.service.admin.order.IOrderConfigService;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import com.niu.shop.service.core.order.param.OrderConfigParam;
import com.niu.shop.service.core.order.vo.OrderConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderConfigServiceImpl implements IOrderConfigService {

    @Resource
    private ICoreOrderConfigService coreOrderConfigService;

    @Override
    public void setConfig(OrderConfigParam orderConfigParam) {
       coreOrderConfigService.setConfig(RequestUtils.siteId(), orderConfigParam);
    }

    @Override
    public OrderConfigVo getConfig() {
       return coreOrderConfigService.getConfig(RequestUtils.siteId());
    }
}
