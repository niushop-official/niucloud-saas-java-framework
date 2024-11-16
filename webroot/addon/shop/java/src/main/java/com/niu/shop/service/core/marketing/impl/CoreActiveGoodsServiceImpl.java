package com.niu.shop.service.core.marketing.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.service.admin.marketing.IShopActiveGoodsService;
import com.niu.shop.service.core.marketing.ICoreActiveGoodsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 店铺营销活动实现
 */
@Service
public class CoreActiveGoodsServiceImpl extends ServiceImpl<ShopActiveGoodsMapper, ShopActiveGoods> implements ICoreActiveGoodsService {


    @Override
    public void insertAll(List<ShopActiveGoods> list) {
        super.saveOrUpdateBatch(list);
    }
}
