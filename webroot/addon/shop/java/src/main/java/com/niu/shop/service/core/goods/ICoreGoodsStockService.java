package com.niu.shop.service.core.goods;

import java.util.Map;

public interface ICoreGoodsStockService {
    /**
     * 批量修改商品库存(goods and sku)
     * @param isInc 是否增加
     * @param goodsMap 商品id和库存
     * @param skuMap sku id和库存
     */
    void batchUpdateStock(Boolean isInc, Map<Integer, Integer> goodsMap, Map<Integer, Integer> skuMap);
}
