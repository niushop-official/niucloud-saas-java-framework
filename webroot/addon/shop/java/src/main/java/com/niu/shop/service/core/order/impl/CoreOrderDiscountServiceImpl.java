package com.niu.shop.service.core.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.shop.entity.order.ShopOrderDiscount;
import com.niu.shop.mapper.order.ShopOrderDiscountMapper;
import com.niu.shop.service.core.order.ICoreOrderDiscountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoreOrderDiscountServiceImpl extends ServiceImpl<ShopOrderDiscountMapper, ShopOrderDiscount> implements ICoreOrderDiscountService {

    @Override
    public void inserAll(List<ShopOrderDiscount> list) {
        super.saveBatch(list);
    }
}
