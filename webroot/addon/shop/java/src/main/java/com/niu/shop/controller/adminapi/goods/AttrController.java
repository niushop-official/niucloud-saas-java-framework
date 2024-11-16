package com.niu.shop.controller.adminapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.IShopGoodsAttrService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsAttrInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsAttrListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 商品参数控制器
 */
@RestController
@RequestMapping("adminapi/shop/goods/attr")
public class AttrController {

    @Resource
    IShopGoodsAttrService goodsAttrService;

    /**
     * 商品参数分页列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsAttrListVo>>
     */
    @GetMapping("")
    public Result<PageResult<ShopGoodsAttrListVo>> page(@Validated PageParam pageParam, @Validated ShopGoodsAttrSearchParam searchParam) {
        PageResult<ShopGoodsAttrListVo> list = goodsAttrService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品参数列表
     *
     * @return Result<List<ShopGoodsAttrListVo>>
     */
    @GetMapping("/list")
    public Result<List<ShopGoodsAttrListVo>> list(@Validated ShopGoodsAttrSearchParam searchParam) {
        List<ShopGoodsAttrListVo> list = goodsAttrService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品参数列表
     *
     * @param id 主键ID
     * @return Result<ShopGoodsAttrInfoVo>
     */
    @GetMapping("/{id}")
    public Result<ShopGoodsAttrInfoVo> info(@Validated @PathVariable Integer id) {
        ShopGoodsAttrInfoVo info = goodsAttrService.info(id);
        return Result.success(info);
    }

    /**
     * 商品参数添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody ShopGoodsAttrParam addParam) {
        goodsAttrService.add(addParam);
        return Result.success();
    }

    /**
     * 商品参数编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable Integer id, @Validated @RequestBody ShopGoodsAttrParam editParam) {
        goodsAttrService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品参数删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable Integer id) {
        goodsAttrService.del(id);
        return Result.success();
    }

    /**
     * 修改商品参数排序号
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/sort")
    public Result<Object> modifySort(@Validated @RequestBody EditGoodsAttrSortParam editParam) {
        goodsAttrService.modifySort(editParam);
        return Result.success();
    }

    /**
     * 修改商品参数名称
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/attr_name")
    public Result<Object> modifyAttrName(@Validated @RequestBody EditGoodsAttrNameParam editParam) {
        goodsAttrService.modifyAttrName(editParam);
        return Result.success();
    }

    /**
     * 修改商品参数值
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/attr_value")
    public Result<Object> modifyAttrValueFormat(@Validated @RequestBody EditGoodsAttrValueParam editParam) {
        goodsAttrService.modifyAttrValueFormat(editParam);
        return Result.success();
    }

}
