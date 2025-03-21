package com.niu.shop.controller.adminapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.delivery.IDeliveryService;
import com.niu.shop.service.admin.delivery.param.DeliveryConfigParam;
import com.niu.shop.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.shop.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.shop.service.core.delivery.vo.DeliverySearchConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 配送方式控制器
 */
@RestController
@RequestMapping("adminapi/shop/delivery")
public class DeliveryController {

    @Resource
    IDeliveryService deliveryService;

    /**
     * 配送方式
     * @return Result<PageResult<ShopDeliveryDeliverListVo>>
     */
    @GetMapping("/deliveryList")
    public Result<List<DeliveryConfigVo>> getDeliveryList() {
        return Result.success(deliveryService.getDeliveryList());
    }

    /**
     * 配送信息设置
     * @return
     */
    @PutMapping("/setConfig")
    public Result<Object> setDeliveryConfig(@Validated @RequestBody DeliveryConfigParam param) {
        deliveryService.setConfig(param.getValue());
        return Result.success();
    }

    /**
     * 获取物流查询接口配置
     * @return Result<PageResult<ShopDeliveryDeliverListVo>>
     */
    @GetMapping("/search")
    public Result<DeliverySearchConfigVo> getDeliverySearchConfig() {
        return Result.success(deliveryService.getDeliverySearchConfig());
    }

    /**
     * 设置物流查询接口配置
     * @return Result
     */
    @PostMapping("/search")
    public Result<Object> setDeliverySearchConfig(@Validated @RequestBody DeliverySearchConfigParam param) {
        deliveryService.setDeliverySearchConfig(param);
        return Result.success();
    }

}
