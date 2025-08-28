package com.niu.shop.controller.adminapi.goods;

import jdk.jfr.Description;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.ShopGoodsServiceParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsServiceSearchParam;
import com.niu.shop.service.admin.goods.IShopGoodsServiceService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsServiceInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsServiceListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 商品服务控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/service")
@Description("商品服务管理")
public class ServiceController {

    @Resource
    IShopGoodsServiceService goodsServiceService;

    /**
     * 商品服务分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsServiceListVo>>
     */
    @GetMapping("")
    @Description("获取商品服务分页列表")
    public Result<PageResult<ShopGoodsServiceListVo>> page(@Validated PageParam pageParam, @Validated ShopGoodsServiceSearchParam searchParam) {
        PageResult<ShopGoodsServiceListVo> list = goodsServiceService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商品服务列表
     *
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopGoodsServiceListVo>>
     */
    @GetMapping("/list")
    @Description("获取商品服务列表")
    public Result<List<ShopGoodsServiceListVo>> list(@Validated PageParam pageParam, @Validated ShopGoodsServiceSearchParam searchParam) {
        List<ShopGoodsServiceListVo> list = goodsServiceService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 商品服务详情
     *
     * @param id 主键ID
     * @return Result<ShopGoodsServiceInfoVo>
     */
    @GetMapping("/{id}")
    @Description("获取商品服务详情")
    public Result<ShopGoodsServiceInfoVo> info(@PathVariable("id") Integer id) {
        ShopGoodsServiceInfoVo info = goodsServiceService.info(id);
        return Result.success(info);
    }

    /**
     * 商品服务添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    @Description("添加商品服务")
    public Result<Object> add(@Validated @RequestBody ShopGoodsServiceParam addParam) {
        goodsServiceService.add(addParam);
        return Result.success();
    }

    /**
     * 商品服务编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    @Description("编辑商品服务")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopGoodsServiceParam editParam) {
        goodsServiceService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品服务删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    @Description("删除商品服务")
    public Result<Object> del(@PathVariable("id") Integer id) {
        goodsServiceService.del(id);
        return Result.success();
    }

}
