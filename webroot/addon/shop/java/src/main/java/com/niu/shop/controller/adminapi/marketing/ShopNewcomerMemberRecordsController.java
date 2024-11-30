package com.niu.shop.controller.adminapi.marketing;

import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.marketing.IShopNewcomerMemberRecordsService;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSetConfigParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 新人专享会员参与记录表控制器
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/shop/active/newcomer")
public class ShopNewcomerMemberRecordsController {

    private final IShopNewcomerMemberRecordsService shopNewcomerMemberRecordsService;

    @GetMapping("/config")
    Result<?> getConfig() {
        return Result.success(shopNewcomerMemberRecordsService.getConfig());
    }

    @PutMapping("/config")
    Result<?> setConfig(ShopNewcomerSetConfigParam param) {
        shopNewcomerMemberRecordsService.setConfig(param);
        return Result.success();
    }

}
