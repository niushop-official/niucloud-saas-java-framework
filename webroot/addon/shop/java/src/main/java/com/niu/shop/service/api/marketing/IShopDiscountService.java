package com.niu.shop.service.api.marketing;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.entity.coupon.ShopCouponGoods;
import com.niu.shop.service.api.marketing.param.DiscountGoodsSearchParam;
import com.niu.shop.service.api.marketing.param.ShopDiscountListParam;
import com.niu.shop.service.api.marketing.vo.ShopDiscountGoodsInfoVo;
import com.niu.shop.service.api.marketing.vo.ShopDiscountGoodsListVo;
import com.niu.shop.service.api.marketing.vo.ShopDiscountListVo;

import java.util.List;

/**
 * 优惠券商品或品类关联服务接口
 */
public interface IShopDiscountService {

    ShopDiscountGoodsInfoVo getInfoByGoods(Integer goodsId);

    JSONArray getDiscountBannerConfig();

    PageResult<ShopDiscountGoodsListVo> getGoodsPage(PageParam pageParam, DiscountGoodsSearchParam searchParam);

    List<ShopDiscountListVo> getList(ShopDiscountListParam shopDiscountListParam);
}