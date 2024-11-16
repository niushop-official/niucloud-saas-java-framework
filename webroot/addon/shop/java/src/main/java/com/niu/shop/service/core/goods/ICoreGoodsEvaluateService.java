package com.niu.shop.service.core.goods;

import com.niu.shop.entity.goods.ShopGoodsEvaluate;

public interface ICoreGoodsEvaluateService {

    /**
     * 添加商品评价
     * @param shopGoodsEvaluate
     */
    Integer addEvaluate(ShopGoodsEvaluate shopGoodsEvaluate);
}
