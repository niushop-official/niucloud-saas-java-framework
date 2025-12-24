package com.niu.shop.service.core.goods;

import java.util.Map;

public interface ICoreGoodsSaleNumService {
    /**
     * 批量修改销量(goods and sku)
     * @param isInc 是否增加
     * @param goodsMap 商品id和库存
     * @param skuMap sku id和销量
     */
    void batchUpdateSaleNum(Boolean isInc, Map<Integer, Integer> goodsMap, Map<Integer, Integer> skuMap);
}
