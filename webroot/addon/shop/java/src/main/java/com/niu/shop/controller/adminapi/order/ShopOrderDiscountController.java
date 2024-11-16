package com.niu.shop.controller.adminapi.order;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountParam;
import com.niu.shop.service.admin.order.param.ShopOrderDiscountSearchParam;
import com.niu.shop.service.admin.order.IShopOrderDiscountService;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderDiscountListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 订单优惠控制器
 */

@RestController
@RequestMapping("adminapi/shop/order/discount")
public class ShopOrderDiscountController {

    @Resource
    IShopOrderDiscountService iShopOrderDiscountService;

    /**
     * 订单优惠列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopOrderDiscountListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<ShopOrderDiscountListVo>> list(@Validated PageParam pageParam,
                                                     @Validated ShopOrderDiscountSearchParam searchParam) {
        PageResult<ShopOrderDiscountListVo> list = iShopOrderDiscountService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 订单优惠详情
     * @param id 主键ID
     * @return Result<ShopOrderDiscountInfoVo>
     */
    @GetMapping("/info")
    public Result<ShopOrderDiscountInfoVo> info(@Validated @RequestParam("id") Integer id) {
        ShopOrderDiscountInfoVo info = iShopOrderDiscountService.info(id);
        return Result.success(info);
    }

    /**
     * 订单优惠添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody ShopOrderDiscountParam addParam) {
        iShopOrderDiscountService.add(addParam);
        return Result.success();
    }

    /**
     * 订单优惠编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody ShopOrderDiscountParam editParam) {
        iShopOrderDiscountService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 订单优惠删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        iShopOrderDiscountService.del(id);
        return Result.success();
    }

}
