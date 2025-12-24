package com.niu.shop.service.api.marketing.param;

import com.niu.shop.service.core.marketing.model.ShopManjianRule;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 满减活动详情
 */
@Data
public class ShopManjianInfoParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsId;

    private Integer skuId;

    private List<ShopManjianRule.GoodsDTO> giftGoods;





}