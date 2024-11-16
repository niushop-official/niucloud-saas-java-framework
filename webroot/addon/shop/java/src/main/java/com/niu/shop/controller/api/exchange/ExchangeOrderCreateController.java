package com.niu.shop.controller.api.exchange;

import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.marketing.IShopPointExchangeService;
import com.niu.shop.service.api.marketing.param.ShopPointCalculateSearchParam;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * 积分兑换控制器
 */

@RestController
@RequestMapping("api/shop")
public class ExchangeOrderCreateController {

    @Resource
    IShopPointExchangeService iShopPointExchangeService;

    @PostMapping("/exchange_order/calculate")
    public Result<?> calculate(@RequestBody OrderCalculateParam param) {
        return Result.success(iShopPointExchangeService.calculate(param));
    }

    @PostMapping("/exchange_order/create")
    public Result<?> createOrder(@RequestBody OrderCreateParam createParam) {
        return Result.success(iShopPointExchangeService.createOrder(createParam));
    }

}
