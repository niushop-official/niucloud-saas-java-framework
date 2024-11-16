package com.niu.shop.service.admin.marketing;

import com.niu.shop.entity.coupon.ShopCouponGoods;

import java.util.List;

/**
 * 优惠券商品或品类关联服务接口
 */
public interface IShopCouponGoodsService {

    void insertAll(List<ShopCouponGoods> list);
}
