package com.niu.shop.controller.adminapi.goods;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import jdk.jfr.Description;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.IGoodsCategoryService;
import com.niu.shop.service.admin.goods.param.EditGoodsCategorySortParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsCategoryParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsCategorySearchParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsCategoryInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsCategoryListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品分类控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods")
@Description("商品分类管理")
public class CategoryController {

    @Resource
    IGoodsCategoryService categoryService;

    /**
     * 商品分类树列表
     *
     * @return Result<List<ShopGoodsCategoryListVo>>
     */
    @GetMapping("/tree")
    @Description("获取商品分类树列表")
    public Result<JSONArray> tree() {
        return Result.success(categoryService.tree());
    }

    /**
     * 商品分类树列表
     *
     * @return Result<List<ShopGoodsCategoryListVo>>
     */
    @GetMapping("/components")
    @Description("获取商品分类组件树")
    public Result<JSONArray> components() {
        ShopGoodsCategorySearchParam param = new ShopGoodsCategorySearchParam();
        param.setIsShow(10);
        return Result.success(categoryService.tree(param));
    }

    /**
     * 商品分类列表
     *
     * @return Result<List<ShopGoodsCategoryListVo>>
     */
    @GetMapping("/category")
    @Description("获取商品分类列表")
    public Result<List<ShopGoodsCategoryListVo>> list(@Validated ShopGoodsCategorySearchParam searchParam) {
        List<ShopGoodsCategoryListVo> list = categoryService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品分类详情
     *
     * @param id 主键ID
     * @return Result<ShopGoodsCategoryInfoVo>
     */
    @GetMapping("/category/{id}")
    @Description("获取商品分类详情")
    public Result<ShopGoodsCategoryInfoVo> info(@PathVariable("id") Integer id) {
        ShopGoodsCategoryInfoVo info = categoryService.info(id);
        return Result.success(info);
    }

    /**
     * 商品分类添加
     *
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("/category")
    @Description("添加商品分类")
    public Result<Object> add(@Validated @RequestBody ShopGoodsCategoryParam addParam) {
        categoryService.add(addParam);
        return Result.success();
    }

    /**
     * 商品分类编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/category/{id}")
    @Description("编辑商品分类")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopGoodsCategoryParam editParam) {
        categoryService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 编辑商品分类
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/category/update")
    @Description("更新商品分类排序")
    public Result<Object> update(@Validated @RequestBody EditGoodsCategorySortParam editParam) {
        categoryService.editGoodsCategorySort(editParam);
        return Result.success();
    }

    /**
     * 商品分类删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/category/{id}")
    @Description("删除商品分类")
    public Result<Object> del(@PathVariable("id") Integer id) {
        categoryService.del(id);
        return Result.success();
    }

    /**
     * 获取商品分类配置
     *
     * @return
     */
    @GetMapping("/category/config")
    @Description("获取商品分类配置")
    public Result<GoodsCategoryConfigVo> getGoodsCategoryConfig() {
        return Result.success(categoryService.getGoodsCategoryConfig());
    }

    /**
     * 设置商品分类配置
     *
     * @return
     */
    @PostMapping("/category/config")
    @Description("设置商品分类配置")
    public Result<Object> setGoodsCategoryConfig(@RequestBody JSONObject data) {
        categoryService.setGoodsCategoryConfig(data);
        return Result.success();
    }
}
