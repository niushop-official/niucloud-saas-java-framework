package com.niu.shop.service.admin.order.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.service.admin.goods.IShopGoodsEvaluateService;
import com.niu.shop.service.admin.order.IOrderConfigService;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import com.niu.shop.service.core.order.param.OrderConfigParam;
import com.niu.shop.service.core.order.vo.OrderConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class OrderConfigServiceImpl implements IOrderConfigService {

    @Resource
    private ICoreOrderConfigService coreOrderConfigService;

    @Resource
    private IShopGoodsEvaluateService goodsEvaluateService;

    @Override
    public void setConfig(OrderConfigParam orderConfigParam) {
        if(orderConfigParam.getEvaluateIsToExamine() == 0)
        {
            goodsEvaluateService.auditEditBatch();
        }
       coreOrderConfigService.setConfig(RequestUtils.siteId(), orderConfigParam);
    }

    @Override
    public OrderConfigVo getConfig() {
       return coreOrderConfigService.getConfig(RequestUtils.siteId());
    }
}
