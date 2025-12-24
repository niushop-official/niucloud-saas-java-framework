package com.niu.shop.service.core.goods.impl;

import com.niu.core.common.exception.CommonException;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.core.goods.ICoreGoodsSaleNumService;
import com.niu.shop.service.core.goods.ICoreGoodsStockService;
import jakarta.annotation.Resource;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class CoreGoodsSaleNumServiceImpl implements ICoreGoodsSaleNumService {
    @Resource
    private ShopGoodsMapper shopGoodsMapper;

    @Resource
    private ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSaleNum(Boolean isInc, Map<Integer, Integer> goodsMap, Map<Integer, Integer> skuMap) {
        if (goodsMap.isEmpty()) {
            return;
        }
        List<ShopGoods> goodsList = shopGoodsMapper.selectByIds(goodsMap.keySet());
        for (ShopGoods goods : goodsList) {
            Integer quantity = goodsMap.get(goods.getGoodsId());
            if (isInc) {
                goods.setSaleNum(goods.getSaleNum() + quantity);
            } else {
                if (goods.getSaleNum() < quantity) {
                    log.error("商品Id: " + goods.getGoodsId() + " 销量不足");
                }else {
                    goods.setSaleNum(goods.getSaleNum() - quantity);
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
                sku.setSaleNum(sku.getSaleNum() + quantity);
            } else {
                if (sku.getSaleNum() < quantity) {
                    log.error("商品skuId: " + sku.getSkuId() + " 销量不足");
                }else {
                    sku.setSaleNum(sku.getSaleNum() - quantity);
                }
            }
        }
        shopGoodsSkuMapper.updateById(skuList);
    }
}
