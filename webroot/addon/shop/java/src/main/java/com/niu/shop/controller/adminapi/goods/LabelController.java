package com.niu.shop.controller.adminapi.goods;

import jdk.jfr.Description;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.IShopGoodsLabelService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品标签控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/label")
@Description("商品标签管理")
public class LabelController {

    @Resource
    IShopGoodsLabelService goodsLabelService;

    /**
     * 商品标签分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsLabelListVo>>
     */
    @GetMapping("")
    @Description("获取商品标签分页列表")
    public Result<PageResult<ShopGoodsLabelListVo>> list(@Validated PageParam pageParam,  @Validated ShopGoodsLabelSearchParam searchParam) {
        PageResult<ShopGoodsLabelListVo> list = goodsLabelService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品标签列表
     *
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsLabelListVo>>
     */
    @GetMapping("/list")
    @Description("获取商品标签列表")
    public Result<List<ShopGoodsLabelListVo>> list(@Validated ShopGoodsLabelSearchParam searchParam) {
        List<ShopGoodsLabelListVo> list = goodsLabelService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品标签详情
     *
     * @param id 主键ID
     * @return Result<ShopGoodsLabelInfoVo>
     */
    @GetMapping("/{id}")
    @Description("获取商品标签详情")
    public Result<ShopGoodsLabelInfoVo> info(@PathVariable("id") Integer id) {
        ShopGoodsLabelInfoVo info = goodsLabelService.info(id);
        return Result.success(info);
    }

    /**
     * 商品标签添加
     *
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    @Description("添加商品标签")
    public Result<Object> add(@Validated @RequestBody ShopGoodsLabelParam addParam) {
        goodsLabelService.add(addParam);
        return Result.success();
    }

    /**
     * 商品标签编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    @Description("编辑商品标签")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopGoodsLabelParam editParam) {
        goodsLabelService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品标签删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    @Description("删除商品标签")
    public Result<Object> del(@PathVariable("id") Integer id) {
        goodsLabelService.del(id);
        return Result.success();
    }

    /**
     * 修改排序
     *
     * @return
     */
    @PutMapping("/sort")
    @Description("修改商品标签排序")
    public Result<Object> modifySort(@Validated @RequestBody EditGoodsLabelSortParam editParam) {
        goodsLabelService.modifySort(editParam);
        return Result.success();
    }

    /**
     * 修改状态
     *
     * @return
     */
    @PutMapping("/status")
    @Description("修改商品标签状态")
    public Result<Object> modifyStatus(@Validated @RequestBody EditGoodsLabelStatusParam editParam) {
        goodsLabelService.modifyStatus(editParam);
        return Result.success();
    }

    /**
     * 复制商品标签
     *
     * @return
     */
    @PostMapping("/copy/{id}")
    @Description("复制商品标签")
    public Result<Object> copy(@PathVariable("id") Integer id) {
        // 参数校验
        if (id == null || id <= 0) {
            return Result.fail("商品标签ID不能为空且必须大于0");
        }
        goodsLabelService.copy(id);
        return Result.success();
    }
}
