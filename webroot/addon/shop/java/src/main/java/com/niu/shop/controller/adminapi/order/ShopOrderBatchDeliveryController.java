package com.niu.shop.controller.adminapi.order;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.order.param.ShopOrderBatchDeliveryParam;
import com.niu.shop.service.admin.order.param.ShopOrderBatchDeliverySearchParam;
import com.niu.shop.service.admin.order.IShopOrderBatchDeliveryService;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderBatchDeliveryListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 订单批量发货控制器
 */

@RestController
@RequestMapping("adminapi/shop/delivery/order")
public class ShopOrderBatchDeliveryController {

    @Resource
    IShopOrderBatchDeliveryService iShopOrderBatchDeliveryService;

    /**
     * 订单批量发货列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopOrderBatchDeliveryListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<ShopOrderBatchDeliveryListVo>> list(@Validated PageParam pageParam,
                                                     @Validated ShopOrderBatchDeliverySearchParam searchParam) {
        PageResult<ShopOrderBatchDeliveryListVo> list = iShopOrderBatchDeliveryService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 订单批量发货详情
     * @param id 主键ID
     * @return Result<ShopOrderBatchDeliveryInfoVo>
     */
    @GetMapping("/info")
    public Result<ShopOrderBatchDeliveryInfoVo> info(@Validated @RequestParam("id") Integer id) {
        ShopOrderBatchDeliveryInfoVo info = iShopOrderBatchDeliveryService.info(id);
        return Result.success(info);
    }

    /**
     * 订单批量发货添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody ShopOrderBatchDeliveryParam addParam) {
        iShopOrderBatchDeliveryService.add(addParam);
        return Result.success();
    }

    /**
     * 订单批量发货编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody ShopOrderBatchDeliveryParam editParam) {
        iShopOrderBatchDeliveryService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 订单批量发货删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        iShopOrderBatchDeliveryService.del(id);
        return Result.success();
    }

}
