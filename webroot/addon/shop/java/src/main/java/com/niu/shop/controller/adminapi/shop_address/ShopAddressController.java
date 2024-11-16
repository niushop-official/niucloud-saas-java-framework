package com.niu.shop.controller.adminapi.shop_address;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.shop_address.param.ShopAddressParam;
import com.niu.shop.service.admin.shop_address.param.ShopAddressSearchParam;
import com.niu.shop.service.admin.shop_address.IShopAddressService;
import com.niu.shop.service.admin.shop_address.vo.ShopAddressInfoVo;
import com.niu.shop.service.admin.shop_address.vo.ShopAddressListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 商家地址库控制器
 */

@RestController
@RequestMapping("adminapi/shop/shop_address")
public class ShopAddressController {

    @Resource
    IShopAddressService iShopAddressService;

    /**
     * 商家地址库列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopAddressListVo>>
     */
    @GetMapping("")
    public Result<PageResult<ShopAddressListVo>> list(@Validated PageParam pageParam, @Validated ShopAddressSearchParam searchParam) {
        PageResult<ShopAddressListVo> list = iShopAddressService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商家地址库详情
     * @param id 主键ID
     * @return Result<ShopAddressInfoVo>
     */
    @GetMapping("/{id}")
    public Result<ShopAddressInfoVo> info(@PathVariable Integer id) {
        ShopAddressInfoVo info = iShopAddressService.info(id);
        return Result.success(info);
    }

    /**
     * 获取默认发货地址
     * @return Result<ShopAddressInfoVo>
     */
    @GetMapping("/default/delivery")
    public Result<ShopAddressInfoVo> defaultDelivery() {
        ShopAddressInfoVo info = iShopAddressService.defaultDelivery();
        return Result.success(info);
    }

    /**
     * 商家地址库添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody ShopAddressParam addParam) {
        iShopAddressService.add(addParam);
        return Result.success();
    }

    /**
     * 商家地址库编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable Integer id, @Validated @RequestBody ShopAddressParam editParam) {
        iShopAddressService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商家地址库删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable Integer id) {
        iShopAddressService.del(id);
        return Result.success();
    }

}
