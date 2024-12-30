package com.niu.shop.service.core.goods;

import com.niu.shop.service.core.order.vo.OrderCreateDataVo;

import java.util.Map;

public interface ICoreGoodsLimitBuyService {

    /**
     * 检测商品限购
     * @param goodsList
     * @return
     */
    String checkGoodsLimitBuy(Integer memberId, Map<String, OrderCreateDataVo.LimitBuy> goodsList);

    /**
     * 获取会员购买商品数量
     * @param siteId
     * @param memberId
     * @param goodsId
     * @return
     */
    Integer getGoodsHasBuyNumber(Integer siteId, Integer memberId, Integer goodsId);
}
