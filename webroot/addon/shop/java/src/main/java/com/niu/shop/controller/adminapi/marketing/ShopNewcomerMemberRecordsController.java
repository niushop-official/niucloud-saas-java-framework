package com.niu.shop.controller.adminapi.marketing;

import jdk.jfr.Description;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.marketing.IShopNewcomerService;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSelectPageParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSelectSkuParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSetConfigParam;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 新人专享会员参与记录表控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/shop/active/newcomer")
@Description("新人专享会员参与记录管理")
public class ShopNewcomerMemberRecordsController {

    private final IShopNewcomerService shopNewcomerMemberRecordsService;

    @GetMapping("/config")
    @Description("获取新人专享配置")
    Result<?> getConfig() {
        return Result.success(shopNewcomerMemberRecordsService.getConfig());
    }

    @PutMapping("/config")
    @Description("设置新人专享配置")
    Result<?> setConfig(@Validated @RequestBody ShopNewcomerSetConfigParam param) {
        shopNewcomerMemberRecordsService.setConfig(param);
        return Result.success();
    }

    @GetMapping("/goods/selectgoodssku")
    @Description("获取可选商品SKU")
    Result<?> getSelectSku(@Validated ShopNewcomerSelectSkuParam param) {
        return Result.success(shopNewcomerMemberRecordsService.getSelectSku(param));
    }

    @GetMapping("/goods/select")
    @Description("获取商品选择分页列表")
    Result<?> select(@Validated PageParam pageParam, @Validated ShopNewcomerSelectPageParam param) {
        return Result.success(shopNewcomerMemberRecordsService.getSelectPage(pageParam, param));
    }

}
