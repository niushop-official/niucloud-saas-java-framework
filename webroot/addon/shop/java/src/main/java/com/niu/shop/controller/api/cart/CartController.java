package com.niu.shop.controller.api.cart;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.cart.IShopCartService;
import com.niu.shop.service.api.cart.param.ShopCartCalculateParam;
import com.niu.shop.service.api.cart.param.ShopCartDeleteParam;
import com.niu.shop.service.api.cart.param.ShopCartParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/shop/cart")
@SaCheckLogin
public class CartController {

    @Resource
    IShopCartService cartService;

    /**
     * 获取购物车列表
     *
     * @return
     */
    @GetMapping("")
    public Result list() {
        return Result.success(cartService.list());
    }

    /**
     * 获取购物车商品列表
     *
     * @return
     */
    @GetMapping("/goods")
    public Result goodsList() {
        return Result.success(cartService.goodsList());
    }

    /**
     * 获取购物车商品列表
     *
     * @return
     */
    @PostMapping("/calculate")
    public Result calculate(@Validated @RequestBody ShopCartCalculateParam param)  {
        return Result.success(cartService.calculate(param));
    }

    /**
     * 添加购物车
     *
     * @return
     */
    @PostMapping("")
    public Result add(@Validated @RequestBody ShopCartParam addParam) {
        cartService.add(addParam);
        return Result.success();
    }

    /**
     * 添加购物车
     *
     * @return
     */
    @PutMapping("")
    public Result edit(@Validated @RequestBody ShopCartParam edieParam) {
        cartService.edit(edieParam);
        return Result.success();
    }

    /**
     * 添加购物车
     *
     * @return
     */
    @PutMapping("/delete")
    public Result edit(@Validated @RequestBody ShopCartDeleteParam param) {
        cartService.del(param);
        return Result.success();
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clear")
    public Result add() {
        cartService.clear();
        return Result.success();
    }

    /**
     * 购物车数量
     *
     * @return
     */
    @GetMapping("/sum")
    public Result getSum(@RequestParam(value = "goods_id", defaultValue = "0") Integer goodsId) {
        return Result.success(cartService.getSum(goodsId));
    }


}
