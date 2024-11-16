package com.niu.shop.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.core.goods.ICoreGoodsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreGoodsServiceImpl implements ICoreGoodsService {

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    /**
     * 增加销量
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    @Override
    public void incSaleNum(Integer siteId, Integer goodsId, Integer skuId, Integer num) {
        UpdateWrapper goods = new UpdateWrapper<ShopGoods>();
        goods.eq("goods_id", goodsId);
        goods.eq("site_id", siteId);
        goods.setSql("sale_num = sale_num + " + num);
        shopGoodsMapper.update(null, goods);

        UpdateWrapper sku = new UpdateWrapper<ShopGoodsSku>();
        sku.eq("sku_id", skuId);
        sku.eq("site_id", siteId);
        sku.setSql("sale_num = sale_num + " + num);
        shopGoodsSkuMapper.update(null, sku);
    }

    /**
     * 减少销量
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    @Override
    public void decSaleNum(Integer siteId, Integer goodsId, Integer skuId, Integer num) {
        UpdateWrapper goods = new UpdateWrapper<ShopGoods>();
        goods.eq("goods_id", goodsId);
        goods.eq("site_id", siteId);
        goods.setSql("sale_num = sale_num - " + num);
        shopGoodsMapper.update(null, goods);

        UpdateWrapper sku = new UpdateWrapper<ShopGoodsSku>();
        sku.eq("sku_id", skuId);
        sku.eq("site_id", siteId);
        sku.setSql("sale_num = sale_num - " + num);
        shopGoodsSkuMapper.update(null, sku);
    }

    /**
     * 增加库存
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    @Override
    public void incStock(Integer siteId, Integer goodsId, Integer skuId, Integer num) {
        UpdateWrapper goods = new UpdateWrapper<ShopGoods>();
        goods.eq("goods_id", goodsId);
        goods.eq("site_id", siteId);
        goods.setSql("stock = stock + " + num);
        shopGoodsMapper.update(null, goods);

        UpdateWrapper sku = new UpdateWrapper<ShopGoodsSku>();
        sku.eq("sku_id", skuId);
        sku.eq("site_id", siteId);
        sku.setSql("stock = stock + " + num);
        shopGoodsSkuMapper.update(null, sku);
    }

    /**
     * 减少库存
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    @Override
    public void decStock(Integer siteId, Integer goodsId, Integer skuId, Integer num) {
        UpdateWrapper goods = new UpdateWrapper<ShopGoods>();
        goods.eq("goods_id", goodsId);
        goods.eq("site_id", siteId);
        goods.setSql("stock = stock - " + num);
        shopGoodsMapper.update(null, goods);

        UpdateWrapper sku = new UpdateWrapper<ShopGoodsSku>();
        sku.eq("sku_id", skuId);
        sku.eq("site_id", siteId);
        sku.setSql("stock = stock - " + num);
        shopGoodsSkuMapper.update(null, sku);
    }
}
