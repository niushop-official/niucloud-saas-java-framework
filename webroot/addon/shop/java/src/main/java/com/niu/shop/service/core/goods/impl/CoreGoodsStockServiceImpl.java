package com.niu.shop.service.core.goods.impl;

import com.niu.core.common.exception.CommonException;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.core.goods.ICoreGoodsStockService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class CoreGoodsStockServiceImpl implements ICoreGoodsStockService {
    @Resource
    private ShopGoodsMapper shopGoodsMapper;

    @Resource
    private ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStock(Boolean isInc, Map<Integer, Integer> goodsMap, Map<Integer, Integer> skuMap) {
        if (goodsMap.isEmpty()) {
            return;
        }
        List<ShopGoods> goodsList = shopGoodsMapper.selectByIds(goodsMap.keySet());
        for (ShopGoods goods : goodsList) {
            Integer quantity = goodsMap.get(goods.getGoodsId());
            if (isInc) {
                goods.setStock(goods.getStock() + quantity);
            } else {
                if (goods.getStock() < quantity) {
                    log.error("商品Id: " + goods.getGoodsId() + " 库存不足");
                }else {
                    goods.setStock(goods.getStock() - quantity);
                }
            }
        }
        shopGoodsMapper.updateById(goodsList);
        if (skuMap.isEmpty()) {
            return;
        }
        List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectByIds(skuMap.keySet());
        for (ShopGoodsSku sku : skuList) {
            Integer quantity = skuMap.get(sku.getSkuId());
            if (isInc) {
                sku.setStock(sku.getStock() + quantity);
            } else {
                if (sku.getStock() < quantity) {
                    log.error("商品skuId: " + sku.getSkuId() + " 库存不足");
                }else {
                    sku.setStock(sku.getStock() - quantity);
                }
            }
        }
        shopGoodsSkuMapper.updateById(skuList);
    }
}
