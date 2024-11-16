package com.niu.shop.controller.adminapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryDeliverParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryDeliverSearchParam;
import com.niu.shop.service.admin.delivery.IShopDeliveryDeliverService;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryDeliverInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryDeliverListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 配送员控制器
 */

@RestController
@RequestMapping("adminapi/shop/delivery/staff")
public class DeliverController {

    @Resource
    IShopDeliveryDeliverService iShopDeliveryDeliverService;

    /**
     * 配送员列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopDeliveryDeliverListVo>>
     */
    @GetMapping("")
    public Result<PageResult<ShopDeliveryDeliverListVo>> list(@Validated PageParam pageParam, @Validated ShopDeliveryDeliverSearchParam searchParam) {
        PageResult<ShopDeliveryDeliverListVo> list = iShopDeliveryDeliverService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 配送员详情
     * @param id 主键ID
     * @return Result<ShopDeliveryDeliverInfoVo>
     */
    @GetMapping("/{id}")
    public Result<ShopDeliveryDeliverInfoVo> info(@PathVariable Integer id) {
        ShopDeliveryDeliverInfoVo info = iShopDeliveryDeliverService.info(id);
        return Result.success(info);
    }

    /**
     * 配送员添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody ShopDeliveryDeliverParam addParam) {
        iShopDeliveryDeliverService.add(addParam);
        return Result.success();
    }

    /**
     * 配送员编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable Integer id, @Validated @RequestBody ShopDeliveryDeliverParam editParam) {
        iShopDeliveryDeliverService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 配送员删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable Integer id) {
        iShopDeliveryDeliverService.del(id);
        return Result.success();
    }

}
