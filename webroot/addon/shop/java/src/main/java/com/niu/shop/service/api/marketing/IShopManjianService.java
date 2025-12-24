package com.niu.shop.service.api.marketing;

import com.niu.shop.service.api.marketing.param.ShopManjianInfoParam;
import com.niu.shop.service.api.marketing.vo.ShopManjianInfoVo;
import com.niu.shop.service.core.marketing.model.ShopManjianRule;

import java.util.List;

/**
 * 满减活动
 */
public interface IShopManjianService {

    List<ShopManjianInfoVo> getManjianInfo(ShopManjianInfoParam param);

    ShopManjianInfoVo getRuleContent(ShopManjianInfoVo vo, List<ShopManjianRule.GoodsDTO> giftGoods);
}