package com.niu.shop.controller.api.goods;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.goods.IGoodsCategoryService;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.api.goods.param.ShopGoodsCategorySearchParam;
import com.niu.shop.service.api.goods.param.ShopGoodsDetailParam;
import com.niu.shop.service.api.goods.param.ShopGoodsSearchParam;
import com.niu.shop.service.api.goods.vo.ShopGoodsCategoryListVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsDetailVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsListVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsSkuVo;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/shop/goods/category")
public class CategoryController {

    @Resource
    IGoodsCategoryService categoryService;

    /**
     * 获取商品分类配置
     *
     * @return
     */
    @GetMapping("/config")
    public Result<GoodsCategoryConfigVo> config() {
        return Result.success(categoryService.getGoodsCategoryConfig());
    }

    /**
     * 获取商品分类树结构
     *
     * @return
     */
    @GetMapping("/tree")
    public Result<JSONArray> tree() {
        return Result.success(categoryService.tree());
    }

    /**
     * 获取商品分类列表
     *
     * @param searchParam
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShopGoodsCategoryListVo>> list(ShopGoodsCategorySearchParam searchParam) {
        return Result.success(categoryService.getList(searchParam));
    }

}
