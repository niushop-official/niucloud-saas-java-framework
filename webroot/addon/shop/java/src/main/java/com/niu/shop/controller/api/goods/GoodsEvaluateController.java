package com.niu.shop.controller.api.goods;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.goods.IGoodsEvaluateService;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.api.goods.param.*;
import com.niu.shop.service.api.goods.vo.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品评价
 */
@RestController
@RequestMapping("api/shop")
public class GoodsEvaluateController {

    @Resource
    IGoodsEvaluateService goodsEvaluateService;

    /**
     * 评价列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/goods/evaluate")
    public Result<PageResult<ShopGoodsEvaluateListVo>> pages(@Validated PageParam pageParam, @Validated ShopGoodsEvaluateSearchParam searchParam) {
        return Result.success(goodsEvaluateService.getPages(pageParam, searchParam));
    }

    /**
     * 评价详情
     * @param id
     * @return
     */
    @GetMapping("/goods/evaluate/{id}")
    public Result<ShopGoodsEvaluateInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(goodsEvaluateService.getInfo(id));
    }

    /**
     * 添加评价
     * @param evaluateParam
     * @return
     */
    @SaCheckLogin
    @PostMapping("/goods/evaluate")
    public Result<Object> add(@Validated @RequestBody ShopGoodsEvaluateParam evaluateParam) {
        goodsEvaluateService.add(evaluateParam);
        return Result.success();
    }

    /**
     * 获取评价数量
     * @param goodsId
     * @return
     */
    @GetMapping("/goods/evaluate/count")
    public Result<Object> count(Integer goodsId) {
        return Result.success(goodsEvaluateService.getCount(goodsId));
    }

    /**
     * 商品详情页评价列表
     * @param goodsId
     * @return
     */
    @GetMapping("/goods/evaluate/list")
    public Result<ShopGoodsEvaluateInGoodsVo> list(@RequestParam(name = "goods_id") Integer goodsId) {
        return Result.success(goodsEvaluateService.getList(goodsId));
    }

    /**
     * 订单页评价
     * @param id
     * @return
     */
    @SaCheckLogin
    @GetMapping("/order/evaluate/{id}")
    public Result<List<ShopGoodsEvaluateDetailVo>> getEvaluate(@PathVariable("id") Integer id) {
        return Result.success(goodsEvaluateService.getDetail(id));
    }
}
