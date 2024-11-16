package com.niu.shop.controller.api.marketing;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.marketing.ICouponService;
import com.niu.shop.service.api.marketing.IShopDiscountService;
import com.niu.shop.service.api.marketing.param.DiscountGoodsSearchParam;
import com.niu.shop.service.api.marketing.param.ShopCouponReceiveParam;
import com.niu.shop.service.api.marketing.param.ShopCouponSearchParam;
import com.niu.shop.service.api.marketing.param.ShopDiscountListParam;
import com.niu.shop.service.api.marketing.vo.ShopCouponInfoVo;
import com.niu.shop.service.api.marketing.vo.ShopCouponListVo;
import com.niu.shop.service.api.marketing.vo.ShopDiscountGoodsListVo;
import com.niu.shop.service.api.marketing.vo.ShopDiscountListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 限时折扣控制器
 */

@RestController
@RequestMapping("api/shop")
public class DiscountController {

    @Resource
    IShopDiscountService shopDiscountService;

    /**
     * 限时折扣配置
     *
     * @return
     */
    @GetMapping("/discount/config")
    public Result<Object> getDiscountBannerConfig() {
        return Result.success(shopDiscountService.getDiscountBannerConfig());
    }

    /**
     * 限时折扣列表
     * @param listParam
     * @return
     */
    @GetMapping("/discount")
    public Result<List<ShopDiscountListVo>> lists(ShopDiscountListParam listParam)  {
        return Result.success(shopDiscountService.getList(listParam));
    }

    /**
     * 限时折扣商品
     * @return
     */
    @GetMapping("/discount/goods")
    public Result<PageResult<ShopDiscountGoodsListVo>> goods(@Validated PageParam pageParam, DiscountGoodsSearchParam searchParam) {
        return Result.success(shopDiscountService.getGoodsPage(pageParam, searchParam));
    }
}
