package com.niu.shop.controller.adminapi;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.stat.IShopStatService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 【请填写功能名称】控制器
 */

@RestController
@RequestMapping("adminapi/shop/stat")
public class ShopStatController {

    @Resource
    IShopStatService iShopStatService;

    /**
     *
     * @return
     */
    @GetMapping("")
    public Result stat() {
        Long startTime = DateUtil.beginOfDay(DateUtil.offsetDay(new DateTime(DateUtil.today()), -6)).getTime() / 1000;
        Long endTime = DateUtil.beginOfDay(DateUtil.offsetDay(new DateTime(DateUtil.today()), 1)).getTime() / 1000;
        return Result.success(iShopStatService.getStatData(startTime, endTime));
    }

    /**
     *
     * @return
     */
    @GetMapping("/total")
    public Result total() {
        return Result.success(iShopStatService.getStat());
    }

    /**
     *
     * @return
     */
    @GetMapping("/today")
    public Result today() {
        DateTime date = new DateTime(DateUtil.today());
        return Result.success(iShopStatService.getStat(DateUtil.beginOfDay(date).getTime() / 1000, DateUtil.endOfDay(date).getTime() / 1000));
    }

    /**
     *
     * @return
     */
    @GetMapping("/yesterday")
    public Result yesterday() {
        DateTime date = new DateTime(DateUtil.yesterday());
        return Result.success(iShopStatService.getStat(DateUtil.beginOfDay(date).getTime() / 1000, DateUtil.endOfDay(date).getTime() / 1000));
    }

    /**
     *
     * @return
     */
    @GetMapping("/order")
    public Result order() {
        return Result.success(iShopStatService.orderStat());
    }

    /**
     *
     * @return
     */
    @GetMapping("/goods")
    public Result goods() {
        return Result.success(iShopStatService.goodsStat());
    }
}
