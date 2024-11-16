package com.niu.shop.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.goods.IShopVirtualGoodsService;
import com.niu.shop.service.admin.goods.param.ShopVirtualGoodsParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("adminapi/shop/goods/virtual")
public class VirtualGoodsController {

    @Resource
    IShopVirtualGoodsService goodsService;

    /**
     * 商品添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
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
    public Result<Object> edit(@PathVariable Integer id, @Validated @RequestBody ShopVirtualGoodsParam editParam) {
        goodsService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品添加/编辑数据
     * @return
     */
    @GetMapping("/init")
    public Result<?> init(@RequestParam(value = "goods_id", defaultValue = "0") Integer goodsId) {
        return Result.success(goodsService.getInit(goodsId));
    }
}
