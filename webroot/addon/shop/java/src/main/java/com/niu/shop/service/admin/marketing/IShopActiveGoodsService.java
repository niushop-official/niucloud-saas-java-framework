package com.niu.shop.service.admin.marketing;

import com.niu.shop.entity.active.ShopActiveGoods;

import java.util.List;

/**
 * 店铺营销活动服务接口
 */
public interface IShopActiveGoodsService {

    void insertAll(List<ShopActiveGoods> list);

}
