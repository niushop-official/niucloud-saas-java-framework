package com.niu.shop.controller.adminapi.goods;

import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.Description;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.IShopGoodsAttrService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsAttrInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsAttrListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品参数控制器
 */
@RestController
@RequestMapping("adminapi/shop/goods/attr")
@Description("商品参数管理")
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
    @Description("获取商品参数分页列表")
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
    @Description("获取商品参数列表")
    public Result<List<ShopGoodsAttrListVo>> list(@Validated ShopGoodsAttrSearchParam searchParam, HttpServletRequest request) {
        String[] paramValues = request.getParameterValues("attr_id_arr[]");
        if(ObjectUtil.isNotNull(paramValues) && paramValues.length > 0){
            List<Integer> attrIdArr = Arrays.stream(paramValues)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            searchParam.setAttrIdArr(attrIdArr);
        }
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
    @Description("获取商品参数详情")
    public Result info(@Validated @PathVariable("id") Integer id) {
        ShopGoodsAttrInfoVo info = goodsAttrService.info(id);
        return Result.success(ObjectUtil.defaultIfNull(info, new LinkedList<>()));
    }

    /**
     * 商品参数添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    @Description("添加商品参数")
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
    @Description("编辑商品参数")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopGoodsAttrParam editParam) {
        goodsAttrService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品参数删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    @Description("删除商品参数")
    public Result<Object> del(@PathVariable("id") Integer id) {
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
    @Description("修改商品参数排序号")
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
    @Description("修改商品参数名称")
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
    @Description("修改商品参数值")
    public Result<Object> modifyAttrValueFormat(@Validated @RequestBody EditGoodsAttrValueParam editParam) {
        goodsAttrService.modifyAttrValueFormat(editParam);
        return Result.success();
    }

}
