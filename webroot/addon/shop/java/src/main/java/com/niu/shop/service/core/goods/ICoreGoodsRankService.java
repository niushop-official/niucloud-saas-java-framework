package com.niu.shop.service.core.goods;

import com.niu.shop.service.core.goods.param.ShopGoodsRankConfigParam;
import com.niu.shop.service.core.goods.vo.ShopGoodsRankConfigVo;

public interface ICoreGoodsRankService {
    void setGoodsRankConfig(ShopGoodsRankConfigParam param);

    ShopGoodsRankConfigVo getGoodsRankConfig(Integer siteId);
}
