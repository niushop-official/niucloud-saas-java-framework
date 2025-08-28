package com.niu.shop.controller.adminapi.order;

import jdk.jfr.Description;
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
@Description("订单批量发货管理")
public class ShopOrderBatchDeliveryController {

    private final IShopOrderBatchDeliveryService iShopOrderBatchDeliveryService;

    @GetMapping
    @Description("获取订单批量发货分页列表")
    public Result<?> list(@Validated PageParam pageParam,
                          @Validated ShopOrderBatchDeliverySearchParam searchParam) {
        return Result.success(iShopOrderBatchDeliveryService.list(pageParam, searchParam));
    }

    @GetMapping("/{id}")
    @Description("获取订单批量发货详情")
    public Result<ShopOrderBatchDeliveryInfoVo> info(@Validated @PathVariable("id") Integer id) {
        return Result.success(iShopOrderBatchDeliveryService.info(id));
    }

    @PutMapping("/add_batch_order_delivery")
    @Description("添加订单批量发货")
    public Result<?> addBatchOrderDelivery(@Validated @RequestBody ShopOrderAddBatchDeliveryParam addParam) {
        iShopOrderBatchDeliveryService.addBatchOrderDelivery(addParam);
        return Result.success();
    }

    @GetMapping("/get_type")
    @Description("获取批量发货类型")
    public Result<?> getBatchType() {
        Map<String, String> result = Arrays.stream(OrderBatchDeliveryTypeEnum.values())
                .collect(Collectors.toMap(OrderBatchDeliveryTypeEnum::getValue, OrderBatchDeliveryTypeEnum::getName));
        return Result.success(result);
    }

    @GetMapping("/get_status")
    @Description("获取批量发货状态")
    public Result<?> getBatchStatus() {
        Map<Integer, String> result = Arrays.stream(OrderBatchDeliveryStatusEnum.values())
                .collect(Collectors.toMap(OrderBatchDeliveryStatusEnum::getValue, OrderBatchDeliveryStatusEnum::getName));
        return Result.success(result);
    }

}
