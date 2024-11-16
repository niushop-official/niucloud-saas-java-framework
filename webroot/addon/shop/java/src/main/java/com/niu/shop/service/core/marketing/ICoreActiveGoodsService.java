package com.niu.shop.service.core.marketing;

import com.niu.shop.entity.active.ShopActiveGoods;

import java.util.List;

/**
 * 营销活动关联商品
 */
public interface ICoreActiveGoodsService {

    void insertAll(List<ShopActiveGoods> list);
}
