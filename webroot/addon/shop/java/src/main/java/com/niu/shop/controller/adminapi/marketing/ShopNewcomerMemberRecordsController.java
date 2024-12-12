package com.niu.shop.controller.adminapi.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.marketing.IShopNewcomerService;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSelectPageParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSelectSkuParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSetConfigParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 新人专享会员参与记录表控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/shop/active/newcomer")
public class ShopNewcomerMemberRecordsController {

    private final IShopNewcomerService shopNewcomerMemberRecordsService;

    @GetMapping("/config")
    Result<?> getConfig() {
        return Result.success(shopNewcomerMemberRecordsService.getConfig());
    }

    @PutMapping("/config")
    Result<?> setConfig(@RequestBody ShopNewcomerSetConfigParam param) {
        shopNewcomerMemberRecordsService.setConfig(param);
        return Result.success();
    }

    @GetMapping("/goods/selectgoodssku")
    Result<?> getSelectSku(ShopNewcomerSelectSkuParam param) {
        return Result.success(shopNewcomerMemberRecordsService.getSelectSku(param));
    }

    @GetMapping("/goods/select")
    Result<?> select(PageParam pageParam, ShopNewcomerSelectPageParam param) {
        return Result.success(shopNewcomerMemberRecordsService.getSelectPage(pageParam, param));
    }

}
