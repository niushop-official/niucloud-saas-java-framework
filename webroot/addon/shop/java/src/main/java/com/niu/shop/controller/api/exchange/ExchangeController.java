package com.niu.shop.controller.api.exchange;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.marketing.IShopPointExchangeService;
import com.niu.shop.service.api.marketing.param.ShopGoodsComponentSearchParam;
import com.niu.shop.service.api.marketing.param.ShopPointExchangeSearchParam;
import com.niu.shop.service.api.marketing.vo.ShopPointExchangeListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("api/shop")
public class ExchangeController {

    @Resource
    IShopPointExchangeService iShopPointExchangeService;

    /**
     * 积分兑换列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopPointExchangeListVo>>
     */
    @GetMapping("/exchange")
    public Result<?> list(@Validated PageParam pageParam,
                          @Validated ShopPointExchangeSearchParam searchParam) {
        PageResult<ShopPointExchangeListVo> list = iShopPointExchangeService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 积分兑换详情
     * @param id 主键ID
     * @return Result<ShopPointExchangeInfoVo>
     */
    @GetMapping("/exchange/goods/{id}")
    public Result<?> info(@Validated @PathVariable("id") Integer id) {
        return Result.success(iShopPointExchangeService.info(id) );
    }

    @GetMapping("/exchange/components")
    public Result<?> component(@Validated ShopGoodsComponentSearchParam searchParam) {
        return Result.success(iShopPointExchangeService.component(searchParam));
    }

    @GetMapping("/exchange/point")
    public Result<?> getPointInfo() {
        return Result.success(iShopPointExchangeService.getPointInfo());
    }

}
