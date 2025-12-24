package com.niu.shop.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.service.core.goods.ICoreGoodsCollectNumService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreGoodsCollectNumServiceImpl implements ICoreGoodsCollectNumService {

    @Resource
    private ShopGoodsMapper shopGoodsMapper;

    @Override
    public void inc(Integer goodsId, Integer collectNum) {
        UpdateWrapper goods = new UpdateWrapper<ShopGoods>();
        goods.eq("goods_id", goodsId);
        goods.setSql("collect_num = collect_num + " + collectNum);
        shopGoodsMapper.update(null, goods);
    }

    @Override
    public void dec(Integer goodsId, Integer collectNum) {
        UpdateWrapper goods = new UpdateWrapper<ShopGoods>();
        goods.eq("goods_id", goodsId);
        goods.setSql("collect_num = collect_num - " + collectNum);
        shopGoodsMapper.update(null, goods);
    }
}
