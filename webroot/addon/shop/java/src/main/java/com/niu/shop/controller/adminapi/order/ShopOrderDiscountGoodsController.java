package com.niu.shop.controller.adminapi.order;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountGoodsParam;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountGoodsSearchParam;
import com.niu.shop.service.admin.order.IShopOrderDiscountGoodsService;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountGoodsInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountGoodsListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 订单项优惠控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/order/discount")
public class ShopOrderDiscountGoodsController {

    @Resource
    IShopOrderDiscountGoodsService iShopOrderDiscountGoodsService;

    /**
     * 订单项优惠列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopOrderDiscountGoodsListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<ShopOrderDiscountGoodsListVo>> list(@Validated PageParam pageParam,
                                                     @Validated ShopOrderDiscountGoodsSearchParam searchParam) {
        PageResult<ShopOrderDiscountGoodsListVo> list = iShopOrderDiscountGoodsService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 订单项优惠详情
     * @param id 主键ID
     * @return Result<ShopOrderDiscountGoodsInfoVo>
     */
    @GetMapping("/info")
    public Result<ShopOrderDiscountGoodsInfoVo> info(@Validated @RequestParam("id") Integer id) {
        ShopOrderDiscountGoodsInfoVo info = iShopOrderDiscountGoodsService.info(id);
        return Result.success(info);
    }

    /**
     * 订单项优惠添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody ShopOrderDiscountGoodsParam addParam) {
        iShopOrderDiscountGoodsService.add(addParam);
        return Result.success();
    }

    /**
     * 订单项优惠编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody ShopOrderDiscountGoodsParam editParam) {
        iShopOrderDiscountGoodsService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 订单项优惠删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        iShopOrderDiscountGoodsService.del(id);
        return Result.success();
    }

}
