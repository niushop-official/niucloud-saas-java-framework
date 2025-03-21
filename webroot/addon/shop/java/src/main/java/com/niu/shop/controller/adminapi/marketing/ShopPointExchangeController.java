package com.niu.shop.controller.adminapi.marketing;

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
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopPointExchangeParam editParam) {
        iShopPointExchangeService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 积分兑换删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        iShopPointExchangeService.del(id);
        return Result.success();
    }

    @GetMapping("/type")
    public Result<?> type() {
        return Result.success(iShopPointExchangeService.type());
    }

    @GetMapping("/status")
    public Result<?> status() {
        return Result.success(iShopPointExchangeService.status());
    }

    @PutMapping("/status/{id}")
    public Result<?> editStatus(@PathVariable("id") Integer id, @Validated @RequestBody ShopPointExchangeEditStatusParam editStatusParam) {
        editStatusParam.setId(id);
        iShopPointExchangeService.editStatus(editStatusParam);
        return Result.success();
    }

    @PutMapping("/sort/{id}")
    public Result<?> modifySort(@PathVariable("id") Integer id, @Validated @RequestBody ShopPointExchangeEditStatusParam editStatusParam) {
        editStatusParam.setId(id);
        iShopPointExchangeService.editStatus(editStatusParam);
        return Result.success();
    }

}
