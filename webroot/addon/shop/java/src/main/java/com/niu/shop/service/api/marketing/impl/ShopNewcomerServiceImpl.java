package com.niu.shop.service.api.marketing.impl;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.api.marketing.IShopNewcomerService;
import com.niu.shop.service.api.marketing.param.ShopNewComerComponentsListParam;
import com.niu.shop.service.api.marketing.param.ShopNewcomerSearchParam;
import com.niu.shop.service.api.marketing.vo.ShopNewComerComponentsListVo;
import com.niu.shop.service.api.marketing.vo.ShopNewcomerListVo;
import com.niu.shop.service.core.marketing.ICoreShopNewcomerService;
import com.niu.shop.service.core.marketing.vo.ShopNewcomerConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopNewcomerServiceImpl implements IShopNewcomerService {
    private final ICoreShopNewcomerService coreShopNewcomerService;

    @Override
    public PageResult<ShopNewcomerListVo> getGoodsPage(PageParam pageParam, ShopNewcomerSearchParam param) {
        return null;
    }

    @Override
    public ShopNewComerComponentsListVo getComponentsList(ShopNewComerComponentsListParam param) {
        return null;
    }

    @Override
    public ShopNewcomerConfigVo getConfig() {
        return coreShopNewcomerService.getConfig();
    }

}
