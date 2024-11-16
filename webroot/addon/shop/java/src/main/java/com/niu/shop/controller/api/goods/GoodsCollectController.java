package com.niu.shop.controller.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.goods.IGoodsCollectService;
import com.niu.shop.service.api.goods.vo.ShopGoodsCollectVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品收藏
 */
@RestController
@RequestMapping("api/shop")
public class GoodsCollectController {

    @Resource
    IGoodsCollectService goodsCollectService;

    /**
     * 收藏列表
     * @param pageParam
     * @return
     */
    @GetMapping("/goods/collect")
    public Result<PageResult<ShopGoodsCollectVo>> getMemberGoodsCollectList(@Validated PageParam pageParam) {
        return Result.success(goodsCollectService.getMemberGoodsCollectList(pageParam));
    }

    /**
     * 添加收藏
     * @param goodsId
     * @return
     */
    @PostMapping("/goods/collect/{goodsId}")
    public Result<Object> addGoodsCollect(@PathVariable("goodsId") Integer goodsId) {
        goodsCollectService.addGoodsCollect(goodsId);
        return Result.success();
    }

    /**
     * 删除收藏
     * @param goodsId
     * @return
     */
    @DeleteMapping("/goods/collect/{goodsId}")
    public Result<Object> cancelGoodsCollect(@PathVariable("goodsId") Integer goodsId) {
        goodsCollectService.cancelGoodsCollect(goodsId);
        return Result.success();
    }
}
