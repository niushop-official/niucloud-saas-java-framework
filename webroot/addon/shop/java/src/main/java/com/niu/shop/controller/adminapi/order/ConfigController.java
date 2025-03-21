package com.niu.shop.controller.adminapi.order;


import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.order.IOrderConfigService;
import com.niu.shop.service.core.order.param.OrderConfigParam;
import com.niu.shop.service.core.order.vo.OrderConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 订单配置制器
 */

@RestController
@RequestMapping("adminapi/shop/order/config")
public class ConfigController {

    @Resource
    IOrderConfigService orderConfigService;

    /**
     * 获取订单设置
     */
    @GetMapping("")
    public Result<OrderConfigVo> get() {
        return Result.success(orderConfigService.getConfig());
    }

    /**
     * 设置订单配置
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> set(@Validated @RequestBody OrderConfigParam orderConfigParam) {
        orderConfigService.setConfig(orderConfigParam);
        return Result.success();
    }



}
