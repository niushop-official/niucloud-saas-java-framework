package com.niu.shop.service.api.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.api.goods.vo.ShopGoodsDetailVo;
import com.niu.shop.service.api.marketing.param.ShopNewComerComponentsListParam;
import com.niu.shop.service.api.marketing.param.ShopNewcomerSearchParam;
import com.niu.shop.service.api.marketing.vo.ShopNewComerComponentsListVo;
import com.niu.shop.service.api.marketing.vo.ShopNewcomerInfoVo;
import com.niu.shop.service.api.marketing.vo.ShopNewcomerListVo;
import com.niu.shop.service.core.marketing.vo.CoreShopNewcomerConfigVo;

import java.util.List;

public interface IShopNewcomerService {
    PageResult<ShopNewcomerListVo> getGoodsPage(PageParam pageParam, ShopNewcomerSearchParam param);

    ShopNewComerComponentsListVo getComponentsList(ShopNewComerComponentsListParam param);

    ShopNewcomerInfoVo getNewcomerInfo(Integer goodsId, Integer skuId);

    List<ShopGoodsDetailVo.Sku> getNewcomerPriceByList(List<ShopGoodsDetailVo.Sku> skuList);

    boolean checkIfNewcomer();

    CoreShopNewcomerConfigVo getConfig();
}
