package com.niu.shop.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.EditGoodsBrandSortParam;
import com.niu.shop.service.admin.goods.param.EditGoodsLabelSortParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsLabelParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsLabelSearchParam;
import com.niu.shop.service.admin.goods.IShopGoodsLabelService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 商品标签控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/label")
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
    public Result<ShopGoodsLabelInfoVo> info(@PathVariable Integer id) {
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
    public Result<Object> edit(@PathVariable Integer id, @Validated @RequestBody ShopGoodsLabelParam editParam) {
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
    public Result<Object> del(@PathVariable Integer id) {
        goodsLabelService.del(id);
        return Result.success();
    }

    /**
     * 修改排序
     *
     * @return
     */
    @PutMapping("/sort")
    public Result<Object> modifySort(@Validated @RequestBody EditGoodsLabelSortParam editParam) {
        goodsLabelService.modifySort(editParam);
        return Result.success();
    }
}
