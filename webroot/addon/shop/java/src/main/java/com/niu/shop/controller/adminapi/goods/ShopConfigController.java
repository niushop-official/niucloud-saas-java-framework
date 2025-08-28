package com.niu.shop.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.shop.service.core.goods.param.SortConfigParam;
import com.niu.shop.service.core.goods.ICoreGoodsConfigService;
import com.niu.shop.service.core.goods.param.ShopConfigParam;
import com.niu.shop.service.core.goods.vo.SortConfigVo;
import jakarta.annotation.Resource;
import jdk.jfr.Description;
import org.springframework.web.bind.annotation.*;

/**
 * 商品设置管理
 */
@RestController
@RequestMapping("adminapi/shop/goods/config")
@Description("商品设置管理")
public class ShopConfigController {

    @Resource
    ICoreGoodsConfigService configService;

    /**
     * 获取搜索配置
     * @return
     */
    @GetMapping("/search")
    public Result searchConfig(){
        return Result.success(configService.getSearchConfig());
    }

    /**
     * 设置搜索配置
     * @param shopConfigParam
     * @return
     */
    @PostMapping("/search")
    public Result setSearchConfig(@RequestBody ShopConfigParam shopConfigParam){
        configService.setSearchConfig(shopConfigParam);
        return Result.success("操作成功");
    }

    /**
     * 获取商品唯一编码配置
     * @return
     */
    @GetMapping("/unique")
    public Result uniqueConfig(){
        return Result.success(configService.getUniqueConfig());
    }

    /**
     * 设置商品唯一编码配置
     * @param shopConfigParam
     * @return
     */
    @PostMapping("/unique")
    public Result setUniqueConfig(@RequestBody ShopConfigParam shopConfigParam){
        configService.setUniqueConfig(shopConfigParam);
        return Result.success("操作成功");
    }

    /**
     * 获取商品排序配置
     * @return
     */
    @GetMapping("/sort")
    public Result<SortConfigVo> sortConfig(){
        return Result.success(configService.getSortConfig());
    }

    /**
     * 设置商品排序配置
     * @param sortConfigParam
     * @return
     */
    @PostMapping("/sort")
    public Result setSortConfig(@RequestBody SortConfigParam sortConfigParam){
        configService.setSortConfig(sortConfigParam);
        return Result.success("操作成功");
    }

}
