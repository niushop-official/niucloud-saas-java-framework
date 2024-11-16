package com.niu.shop.service.api.marketing.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.shop.entity.coupon.ShopCouponGoods;
import com.niu.shop.mapper.coupon.ShopCouponGoodsMapper;
import com.niu.shop.service.api.marketing.IShopCouponGoodsService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 优惠券商品或品类关联实现
 */
@Service
public class ShopCouponGoodsServiceImpl  extends ServiceImpl<ShopCouponGoodsMapper, ShopCouponGoods> implements IShopCouponGoodsService {


    @Override
    public void insertAll(List<ShopCouponGoods> list) {
        super.saveOrUpdateBatch(list);
    }
}
