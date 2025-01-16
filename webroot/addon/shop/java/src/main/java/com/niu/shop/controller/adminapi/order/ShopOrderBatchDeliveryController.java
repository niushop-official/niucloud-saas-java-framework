package com.niu.shop.controller.adminapi.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.shop.enums.order.OrderBatchDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderBatchDeliveryTypeEnum;
import com.niu.shop.service.admin.order.IShopOrderBatchDeliveryService;
import com.niu.shop.service.admin.order.param.ShopOrderAddBatchDeliveryParam;
import com.niu.shop.service.admin.order.param.ShopOrderBatchDeliverySearchParam;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单批量发货控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/adminapi/shop/order_batch_delivery")
public class ShopOrderBatchDeliveryController {

    private final IShopOrderBatchDeliveryService iShopOrderBatchDeliveryService;

    @GetMapping
    public Result<?> list(@Validated PageParam pageParam,
                          @Validated ShopOrderBatchDeliverySearchParam searchParam) {
        return Result.success(iShopOrderBatchDeliveryService.list(pageParam, searchParam));
    }

    @GetMapping("/{id}")
    public Result<ShopOrderBatchDeliveryInfoVo> info(@Validated @PathVariable("id") Integer id) {
        return Result.success(iShopOrderBatchDeliveryService.info(id));
    }

    @PutMapping("/add_batch_order_delivery")
    public Result<?> addBatchOrderDelivery(@Validated @RequestBody ShopOrderAddBatchDeliveryParam addParam) {
        iShopOrderBatchDeliveryService.addBatchOrderDelivery(addParam);
        return Result.success();
    }

    @GetMapping("/get_type")
    public Result<?> getBatchType() {
        Map<String, String> result = Arrays.stream(OrderBatchDeliveryTypeEnum.values())
                .collect(Collectors.toMap(OrderBatchDeliveryTypeEnum::getValue, OrderBatchDeliveryTypeEnum::getName));
        return Result.success(result);
    }

    @GetMapping("/get_status")
    public Result<?> getBatchStatus() {
        Map<Integer, String> result = Arrays.stream(OrderBatchDeliveryStatusEnum.values())
                .collect(Collectors.toMap(OrderBatchDeliveryStatusEnum::getValue, OrderBatchDeliveryStatusEnum::getName));
        return Result.success(result);
    }

}
