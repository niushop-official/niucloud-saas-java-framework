package com.niu.shop.controller.adminapi.marketing;

import com.niu.shop.service.admin.marketing.param.PointExchangeBatchParam;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeSelectListVo;
import jdk.jfr.Description;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeEditStatusParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeSearchParam;
import com.niu.shop.service.admin.marketing.IShopPointExchangeService;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 积分兑换控制器
 */

@RestController
@RequestMapping("adminapi/shop/active/exchange")
@Description("积分兑换管理")
public class ShopPointExchangeController {

    @Resource
    IShopPointExchangeService iShopPointExchangeService;

    /**
     * 积分兑换列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopPointExchangeListVo>>
     */
    @GetMapping
    @Description("获取积分兑换分页列表")
    public Result<PageResult<ShopPointExchangeListVo>> list(@Validated PageParam pageParam,
                                                     @Validated ShopPointExchangeSearchParam searchParam) {
        PageResult<ShopPointExchangeListVo> list = iShopPointExchangeService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 积分兑换详情
     * @param id 主键ID
     * @return Result<ShopPointExchangeInfoVo>
     */
    @GetMapping("/{id}")
    @Description("获取积分兑换详情")
    public Result<ShopPointExchangeInfoVo> info(@Validated @PathVariable("id") Integer id) {
        ShopPointExchangeInfoVo info = iShopPointExchangeService.info(id);
        return Result.success(info);
    }

    /**
     * 积分兑换添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping
    @Description("添加积分兑换")
    public Result<Object> add(@Validated @RequestBody ShopPointExchangeParam addParam) {
        iShopPointExchangeService.add(addParam);
        return Result.success();
    }

    /**
     * 积分兑换编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    @Description("编辑积分兑换")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody ShopPointExchangeParam editParam) {
        iShopPointExchangeService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 积分兑换删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    @Description("删除积分兑换")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        iShopPointExchangeService.del(id);
        return Result.success();
    }

    @GetMapping("/type")
    @Description("获取积分兑换类型")
    public Result<?> type() {
        return Result.success(iShopPointExchangeService.type());
    }

    @GetMapping("/status")
    @Description("获取积分兑换状态")
    public Result<?> status() {
        return Result.success(iShopPointExchangeService.status());
    }

    @PutMapping("/status/{id}")
    @Description("编辑积分兑换状态")
    public Result<?> editStatus(@Validated @PathVariable("id") Integer id, @Validated @RequestBody ShopPointExchangeEditStatusParam editStatusParam) {
        editStatusParam.setId(id);
        iShopPointExchangeService.editStatus(editStatusParam);
        return Result.success();
    }

    @PutMapping("/sort/{id}")
    @Description("修改积分兑换排序")
    public Result<?> modifySort(@Validated @PathVariable("id") Integer id, @Validated @RequestBody ShopPointExchangeEditStatusParam editStatusParam) {
        editStatusParam.setId(id);
        iShopPointExchangeService.editStatus(editStatusParam);
        return Result.success();
    }

    @PostMapping("/batchDelete")
    @Description("批量删除积分兑换")
    public Result<?> batchDelete(@Validated @RequestBody PointExchangeBatchParam param) {
        iShopPointExchangeService.batchDelete(param);
        return Result.success();
    }

    @PostMapping("/batchUp")
    @Description("积分兑换批量上架")
    public Result<?> batchUp(@Validated @RequestBody PointExchangeBatchParam param) {
        iShopPointExchangeService.batchUp(param);
        return Result.success();
    }

    @PostMapping("/batchDown")
    @Description("积分兑换批量下架")
    public Result<?> batchDown(@Validated @RequestBody PointExchangeBatchParam param) {
        iShopPointExchangeService.batchDown(param);
        return Result.success();
    }

    @GetMapping("/select")
    @Description("积分商城分页列表（用于弹框选择）")
    public Result<ShopPointExchangeSelectListVo> select(@Validated PageParam pageParam, @Validated ShopPointExchangeSearchParam searchParam) {
        return Result.success(iShopPointExchangeService.getSelectPage(pageParam, searchParam));
    }
}
