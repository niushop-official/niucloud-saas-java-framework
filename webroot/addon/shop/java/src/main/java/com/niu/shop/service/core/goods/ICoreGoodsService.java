package com.niu.shop.service.core.goods;

public interface ICoreGoodsService {

    /**
     * 增加销量
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    void incSaleNum(Integer siteId, Integer goodsId, Integer skuId, Integer num);

    /**
     * 减少销量
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    void decSaleNum(Integer siteId, Integer goodsId, Integer skuId, Integer num);

    /**
     * 增加库存
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    void incStock(Integer siteId, Integer goodsId, Integer skuId, Integer num);

    /**
     * 减少库存
     *
     * @param siteId
     * @param goodsId
     * @param skuId
     * @param num
     */
    void decStock(Integer siteId, Integer goodsId, Integer skuId, Integer num);
}
