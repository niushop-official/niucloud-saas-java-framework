package com.niu.shop.service.api.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.api.marketing.param.ShopNewComerComponentsListParam;
import com.niu.shop.service.api.marketing.param.ShopNewcomerSearchParam;
import com.niu.shop.service.api.marketing.vo.ShopNewComerComponentsListVo;
import com.niu.shop.service.api.marketing.vo.ShopNewcomerListVo;
import com.niu.shop.service.core.marketing.vo.ShopNewcomerConfigVo;

public interface IShopNewcomerService {
    PageResult<ShopNewcomerListVo> getGoodsPage(PageParam pageParam, ShopNewcomerSearchParam param);

    ShopNewComerComponentsListVo getComponentsList(ShopNewComerComponentsListParam param);

    ShopNewcomerConfigVo getConfig();
}
