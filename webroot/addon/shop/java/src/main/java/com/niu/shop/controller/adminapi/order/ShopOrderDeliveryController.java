package com.niu.shop.controller.adminapi.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.service.admin.order.IShopOrderDeliveryService;
import com.niu.shop.service.admin.order.IShopOrderService;
import com.niu.shop.service.admin.order.param.EditPriceParam;
import com.niu.shop.service.admin.order.param.SetShopRemarkParam;
import com.niu.shop.service.admin.order.param.ShopOrderSearchParam;
import com.niu.shop.service.admin.order.vo.ShopOrderDeliveryInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderDeliveryListVo;
import com.niu.shop.service.admin.order.vo.ShopOrderInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderListVo;
import com.niu.shop.service.core.order.param.OrderDeliveryParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 订单配送相关控制器
 */

@RestController
@RequestMapping("adminapi/shop/order")
public class ShopOrderDeliveryController {

    @Resource
    IShopOrderDeliveryService shopOrderDeliveryService;

    @PutMapping("/delivery")
    public Result<String> orderDelivery(@Validated @RequestBody OrderDeliveryParam orderDeliveryParam) {
        shopOrderDeliveryService.delivery(orderDeliveryParam);
        return Result.success("发货成功");
    }

    @GetMapping("/delivery_type")
    public Result<Map<String, String>> getDeliveryType(@RequestParam(name = "delivery_type", defaultValue = "") String deliveryType){
        return Result.success(OrderDeliveryTypeEnum.getMapByType(deliveryType));
    }

    @GetMapping("/delivery/package")
    public Result<ShopOrderDeliveryInfoVo> getOrderPackage(Integer id, String mobile) {
        return Result.success(shopOrderDeliveryService.getDeliveryPackage(id, mobile));
    }

    @GetMapping("'delivery/package/list")
    public Result<List<ShopOrderDeliveryListVo>> getDeliveryPackageList(Integer orderId) {

        return Result.success(shopOrderDeliveryService.getDeliveryPackageList(orderId));
    }




}