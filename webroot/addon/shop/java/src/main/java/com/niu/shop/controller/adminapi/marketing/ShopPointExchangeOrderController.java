package com.niu.shop.controller.adminapi.marketing;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeOrderParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeOrderSearchParam;
import com.niu.shop.service.admin.marketing.IShopPointExchangeOrderService;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeOrderInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeOrderListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 积分兑换订单控制器
 */

@RestController
@RequestMapping("adminapi/shop/order/point")
public class ShopPointExchangeOrderController {

    @Resource
    IShopPointExchangeOrderService iShopPointExchangeOrderService;

    /**
     * 积分兑换订单列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopPointExchangeOrderListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<ShopPointExchangeOrderListVo>> list(@Validated PageParam pageParam,
                                                     @Validated ShopPointExchangeOrderSearchParam searchParam) {
        PageResult<ShopPointExchangeOrderListVo> list = iShopPointExchangeOrderService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 积分兑换订单详情
     * @param id 主键ID
     * @return Result<ShopPointExchangeOrderInfoVo>
     */
    @GetMapping("/info")
    public Result<ShopPointExchangeOrderInfoVo> info(@Validated @RequestParam("id") Integer id) {
        ShopPointExchangeOrderInfoVo info = iShopPointExchangeOrderService.info(id);
        return Result.success(info);
    }

    /**
     * 积分兑换订单添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody ShopPointExchangeOrderParam addParam) {
        iShopPointExchangeOrderService.add(addParam);
        return Result.success();
    }

    /**
     * 积分兑换订单编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody ShopPointExchangeOrderParam editParam) {
        iShopPointExchangeOrderService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 积分兑换订单删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        iShopPointExchangeOrderService.del(id);
        return Result.success();
    }

}
