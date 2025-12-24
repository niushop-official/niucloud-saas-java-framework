package com.niu.shop.controller.api.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryInfoVo;
import com.niu.shop.service.api.order.IOrderCreateService;
import com.niu.shop.service.core.delivery.param.LatlngParam;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/shop")
@SaCheckLogin
public class OrderCreateController {

    @Resource
    IOrderCreateService orderCreateService;

    /**
     * 计算
     *
     * @return
     */
    @PostMapping("/order_create/calculate")
    public Result<OrderCreateDataVo> calculate(@Validated @RequestBody OrderCalculateParam param) {
        return Result.success(orderCreateService.calculate(param));
    }

    /**
     * 创建订单
     *
     * @return
     */
    @PostMapping("/order_create/create")
    public Result create(@Validated @RequestBody OrderCreateParam param) {
        return Result.success(orderCreateService.create(param));
    }


    /**
     * 查询优惠券
     *
     * @return
     */
    @GetMapping("/order_create/coupon")
    public Result getCoupon(@RequestParam(name = "order_key") String orderKey) {
        return Result.success(orderCreateService.getCoupon(orderKey));
    }

    /**
     * 查询自提点
     *
     * @return
     */
    @GetMapping("/order_create/store")
    public Result getStore(@RequestParam(name = "latlng[lat]") BigDecimal lat, @RequestParam("latlng[lng]") BigDecimal lng) {
        return Result.success(orderCreateService.getStore(new LatlngParam(lat, lng)));
    }

    /**
     * 获取本地配送数据
     * @return
     */
    @GetMapping("/order_create/local")
    public Result getLocalDeliveryConfig(){
        ShopDeliveryLocalDeliveryInfoVo shopDeliveryLocalDeliveryInfoVo = orderCreateService.getLocal();
        if(ObjectUtil.isNull(shopDeliveryLocalDeliveryInfoVo)){
            return Result.success(new ArrayList<>());
        }else{
            return Result.success(shopDeliveryLocalDeliveryInfoVo);
        }
    }
}
