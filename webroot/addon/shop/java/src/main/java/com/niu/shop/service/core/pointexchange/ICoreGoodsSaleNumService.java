package com.niu.shop.service.core.pointexchange;

import com.niu.shop.service.core.pointexchange.param.ExchangeDataChangeParam;

public interface ICoreGoodsSaleNumService {

    /**
     * 增加
     */
    void inc(Integer id, ExchangeDataChangeParam data);

    /**
     * 减少
     */
    void dec(Integer id, ExchangeDataChangeParam data);

    /**
     * 改变库存
     *
     * @param id
     * @param skuId
     * @param stock
     */
    void changeStock(Integer id, Integer skuId, Integer stock);
}
