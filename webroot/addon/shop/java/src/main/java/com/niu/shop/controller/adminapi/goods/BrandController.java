package com.niu.shop.controller.adminapi.goods;

import jdk.jfr.Description;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.EditGoodsBrandSortParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsBrandParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsBrandSearchParam;
import com.niu.shop.service.admin.goods.IShopGoodsBrandService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsBrandInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsBrandListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品品牌控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/brand")
@Description("商品品牌管理")
public class BrandController {

    @Resource
    IShopGoodsBrandService goodsBrandService;

    /**
     * 商品品牌分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsBrandListVo>>
     */
    @GetMapping("")
    @Description("获取商品品牌分页列表")
    public Result<PageResult<ShopGoodsBrandListVo>> page(@Validated PageParam pageParam, @Validated ShopGoodsBrandSearchParam searchParam) {
        PageResult<ShopGoodsBrandListVo> list = goodsBrandService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品品牌列表
     *
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsBrandListVo>>
     */
    @GetMapping("/list")
    @Description("获取商品品牌列表")
    public Result<List<ShopGoodsBrandListVo>> list(@Validated ShopGoodsBrandSearchParam searchParam) {
        List<ShopGoodsBrandListVo> list = goodsBrandService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品品牌详情
     *
     * @param id 主键ID
     * @return Result<ShopGoodsBrandInfoVo>
     */
    @GetMapping("/{id}")
    @Description("获取商品品牌详情")
    public Result<ShopGoodsBrandInfoVo> info(@PathVariable("id") Integer id) {
        ShopGoodsBrandInfoVo info = goodsBrandService.info(id);
        return Result.success(info);
    }

    /**
     * 商品品牌添加
     *
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    @Description("添加商品品牌")
    public Result<Object> add(@Validated @RequestBody ShopGoodsBrandParam addParam) {
        goodsBrandService.add(addParam);
        return Result.success();
    }

    /**
     * 商品品牌编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    @Description("编辑商品品牌")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopGoodsBrandParam editParam) {
        goodsBrandService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品品牌删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    @Description("删除商品品牌")
    public Result<Object> del(@PathVariable("id") Integer id) {
        goodsBrandService.del(id);
        return Result.success();
    }

    /**
     * 修改排序
     *
     * @return
     */
    @PutMapping("/sort")
    @Description("修改商品品牌排序")
    public Result<Object> modifySort(@Validated @RequestBody EditGoodsBrandSortParam editParam) {
        goodsBrandService.modifySort(editParam);
        return Result.success();
    }

}
