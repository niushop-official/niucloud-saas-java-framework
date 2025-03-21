package com.niu.shop.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderLog;
import com.niu.shop.mapper.order.ShopOrderLogMapper;
import com.niu.shop.service.core.order.ICoreOrderLogService;
import com.niu.shop.service.core.order.param.AddOrderLogParam;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class CoreOrderLogServiceImpl implements ICoreOrderLogService {

    @Resource
    ShopOrderLogMapper shopOrderLogMapper;

    @Override
    public void add(AddOrderLogParam data) {
        ShopOrderLog model = new ShopOrderLog();
        BeanUtil.copyProperties(data, model);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        shopOrderLogMapper.insert(model);
    }
}
