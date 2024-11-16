package com.niu.shop.service.api.marketing;


import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.api.marketing.param.ShopGoodsComponentSearchParam;
import com.niu.shop.service.api.marketing.param.ShopPointCalculateSearchParam;
import com.niu.shop.service.api.marketing.param.ShopPointExchangeSearchParam;
import com.niu.shop.service.api.marketing.vo.ShopPointExchangeCalculateVo;
import com.niu.shop.service.api.marketing.vo.ShopPointExchangeInfoVo;
import com.niu.shop.service.api.marketing.vo.ShopPointExchangeListVo;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;
import com.niu.shop.service.core.pointexchange.vo.OrderCreateDataVo;

import java.util.List;
import java.util.Map;

public interface IShopPointExchangeService {

    PageResult<ShopPointExchangeListVo> list(PageParam pageParam, ShopPointExchangeSearchParam searchParam);

    ShopPointExchangeInfoVo info(Integer id);

    List<ShopPointExchangeListVo> component(ShopGoodsComponentSearchParam searchParam);

    Map<String, Integer> getPointInfo();

    OrderCreateDataVo calculate(OrderCalculateParam searchParam);

    OrderCreateResultVo createOrder(OrderCreateParam param);
}
