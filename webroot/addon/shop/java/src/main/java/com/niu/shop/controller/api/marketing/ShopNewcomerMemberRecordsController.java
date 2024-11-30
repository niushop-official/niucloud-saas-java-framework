package com.niu.shop.controller.api.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.marketing.IShopNewcomerService;
import com.niu.shop.service.api.marketing.param.ShopNewComerComponentsListParam;
import com.niu.shop.service.api.marketing.param.ShopNewcomerSearchParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 新人专享会员参与记录表控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/shop/newcomer")
public class ShopNewcomerMemberRecordsController {

    private final IShopNewcomerService shopNewcomerService;

    @GetMapping("/goods")
    Result<?> pages(PageParam pageParam, ShopNewcomerSearchParam param) {
        return Result.success(shopNewcomerService.getGoodsPage(pageParam, param));
    }

    @GetMapping("/goods/components")
    Result<?> componentsList(ShopNewComerComponentsListParam param) {
        return Result.success(shopNewcomerService.getComponentsList(param));
    }

    @GetMapping("/config")
    Result<?> getConfig() {
        return Result.success(shopNewcomerService.getConfig());
    }

}
