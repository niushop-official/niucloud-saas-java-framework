package com.niu.shop.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.shop.entity.goods.ShopGoodsEvaluate;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.mapper.goods.ShopGoodsEvaluateMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.goods.ICoreGoodsEvaluateService;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class CoreGoodsEvaluateServiceImpl implements ICoreGoodsEvaluateService {

    @Resource
    private ShopGoodsEvaluateMapper shopGoodsEvaluateMapper;

    @Resource
    private ShopOrderMapper shopOrderMapper;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Override
    public Integer addEvaluate(ShopGoodsEvaluate shopGoodsEvaluate) {
        shopGoodsEvaluateMapper.insert(shopGoodsEvaluate);

        if (shopGoodsEvaluate.getOrderId() > 0) {
            ShopOrder shopOrder = new ShopOrder();
            shopOrder.setIsEvaluate(1);
            shopOrderMapper.update(shopOrder, new QueryWrapper<ShopOrder>().eq("order_id", shopGoodsEvaluate.getOrderId()));
        }

        if (shopGoodsEvaluate.getIsAudit() == 2) {
            GoodsAddStatParam addStatParam = new GoodsAddStatParam();
            addStatParam.setGoodsId(shopGoodsEvaluate.getGoodsId());
            addStatParam.setSiteId(shopGoodsEvaluate.getSiteId());
            addStatParam.setEvaluateNum(1);
            coreGoodsStatService.addStat(addStatParam);
        }

        return shopGoodsEvaluate.getEvaluateId();
    }
}
