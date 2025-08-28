package com.niu.shop.controller.adminapi.delivery;

import jdk.jfr.Description;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryLocalDeliveryParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryLocalDeliverySearchParam;
import com.niu.shop.service.admin.delivery.IShopDeliveryLocalDeliveryService;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryLocalDeliveryListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 同城配送控制器
 */

@RestController
@RequestMapping("adminapi/shop")
@Description("同城配送管理")
public class LocalController {

    @Resource
    IShopDeliveryLocalDeliveryService iShopDeliveryLocalDeliveryService;

    /**
     * 同城配送详情
     *
     * @return Result<ShopDeliveryLocalDeliveryInfoVo>
     */
    @GetMapping("/local")
    @Description("获取同城配送配置")
    public Result<ShopDeliveryLocalDeliveryInfoVo> getLocal() {
        ShopDeliveryLocalDeliveryInfoVo info = iShopDeliveryLocalDeliveryService.getLocal();
        return Result.success(info);
    }

    /**
     * 设置同城配送
     *
     * @param param 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/local")
    @Description("设置同城配送配置")
    public Result<Object> setLocal(@Validated @RequestBody ShopDeliveryLocalDeliveryParam param) {
        iShopDeliveryLocalDeliveryService.setLocal(param);
        return Result.success();
    }

    /**
     * 查看三方配送初始化信息
     */
    @GetMapping("/third/init")
    public Result<?> getThirdPartyInit() {
        return Result.success(iShopDeliveryLocalDeliveryService.getThirdTypeConfig());
    }


}
