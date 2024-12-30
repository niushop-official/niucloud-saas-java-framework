package com.niu.shop.service.api.marketing;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.api.marketing.param.DiscountGoodsSearchParam;
import com.niu.shop.service.api.marketing.param.ShopDiscountListParam;
import com.niu.shop.service.api.marketing.param.ShopManjianInfoParam;
import com.niu.shop.service.api.marketing.vo.*;

import java.util.List;

/**
 * 满减活动
 */
public interface IShopManjianService {

    ShopManjianInfoVo getManjianInfo(ShopManjianInfoParam param);

    ShopManjianInfoVo getRuleContent(ShopManjianInfoVo vo);
}