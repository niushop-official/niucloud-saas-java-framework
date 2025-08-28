package com.niu.shop.controller.adminapi.goods;

import jdk.jfr.Description;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.goods.IShopVirtualGoodsService;
import com.niu.shop.service.admin.goods.param.ShopVirtualGoodsParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("adminapi/shop/goods/virtual")
@Description("虚拟商品管理")
public class VirtualGoodsController {

    @Resource
    IShopVirtualGoodsService goodsService;

    /**
     * 商品添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    @Description("添加虚拟商品")
    public Result<Object> add(@Validated @RequestBody ShopVirtualGoodsParam addParam) {
        goodsService.add(addParam);
        return Result.success();
    }

    /**
     * 商品编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    @Description("编辑虚拟商品")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopVirtualGoodsParam editParam) {
        goodsService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品添加/编辑数据
     * @return
     */
    @GetMapping("/init")
    @Description("获取虚拟商品初始化数据")
    public Result<?> init(@RequestParam(value = "goods_id", defaultValue = "0") Integer goodsId) {
        return Result.success(goodsService.getInit(goodsId));
    }
}
