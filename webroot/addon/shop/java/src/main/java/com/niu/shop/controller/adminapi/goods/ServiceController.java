package com.niu.shop.controller.adminapi.goods;

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
import javax.annotation.Resource;
import java.util.List;

/**
 * 商品服务控制器
 */

@RestController
@RequestMapping("adminapi/shop/goods/service")
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
    public Result<ShopGoodsServiceInfoVo> info(@PathVariable Integer id) {
        ShopGoodsServiceInfoVo info = goodsServiceService.info(id);
        return Result.success(info);
    }

    /**
     * 商品服务添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
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
    public Result<Object> edit(@PathVariable Integer id, @Validated @RequestBody ShopGoodsServiceParam editParam) {
        goodsServiceService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商品服务删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable Integer id) {
        goodsServiceService.del(id);
        return Result.success();
    }

}
