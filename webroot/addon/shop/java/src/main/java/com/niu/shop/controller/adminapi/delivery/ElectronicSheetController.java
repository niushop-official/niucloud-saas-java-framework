package com.niu.shop.controller.adminapi.delivery;

import cn.hutool.core.collection.CollectionUtil;
import jdk.jfr.Description;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.delivery.IShopDeliveryElectronicSheetService;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryElectronicSheetListsParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryElectronicSheetPagesParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryElectronicSheetParam;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryElectronicSheetInfoVo;
import com.niu.shop.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.shop.service.core.delivery.vo.ElectronicSheetPrintVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 电子面单控制器
 */

@RestController
@RequestMapping("adminapi/shop/electronic_sheet")
@Description("电子面单管理")
public class ElectronicSheetController {

    @Resource
    IShopDeliveryElectronicSheetService iShopDeliveryElectronicSheetService;

    /**
     * 电子面单列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < ShopDeliveryElectronicSheetListVo>>
     */
    @GetMapping("/list")
    @Description("获取电子面单列表")
    public Result<?> list(@Validated PageParam pageParam, @Validated ShopDeliveryElectronicSheetListsParam searchParam) {
        return Result.success(iShopDeliveryElectronicSheetService.lists(pageParam, searchParam));
    }

    @GetMapping
    @Description("获取电子面单分页列表")
    public Result<?> pages(@Validated PageParam pageParam, ShopDeliveryElectronicSheetPagesParam pagesParam) {
        return Result.success(iShopDeliveryElectronicSheetService.pages(pageParam, pagesParam));
    }

    /**
     * 电子面单详情
     *
     * @param id 主键ID
     * @return Result<ShopDeliveryElectronicSheetInfoVo>
     */
    @GetMapping("/info")
    @Description("获取电子面单详情")
    public Result<ShopDeliveryElectronicSheetInfoVo> info(@Validated @RequestParam("id") Integer id) {
        ShopDeliveryElectronicSheetInfoVo info = iShopDeliveryElectronicSheetService.info(id);
        return Result.success(info);
    }

    @GetMapping("/{id}")
    @Description("获取电子面单详细信息")
    public Result<?> detail(@Validated @PathVariable("id") Integer id) {
        return Result.success(iShopDeliveryElectronicSheetService.info(id));
    }


    /**
     * 电子面单添加
     *
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("/add")
    @Description("添加电子面单")
    public Result<Object> add(@Validated @RequestBody ShopDeliveryElectronicSheetParam addParam) {
        iShopDeliveryElectronicSheetService.add(addParam);
        return Result.success();
    }

    @PostMapping
    @Description("添加电子面单")
    public Result<?> addElectronicSheet(@Validated @RequestBody ShopDeliveryElectronicSheetParam addParam) {
        iShopDeliveryElectronicSheetService.add(addParam);
        return Result.success();
    }

    /**
     * 电子面单编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    @Description("编辑电子面单")
    public Result<Object> edit(Integer id, @Validated @RequestBody ShopDeliveryElectronicSheetParam editParam) {
        iShopDeliveryElectronicSheetService.edit(id, editParam);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Description("编辑电子面单")
    public Result<?> editElectronicSheet(@PathVariable("id") Integer id, @Validated @RequestBody ShopDeliveryElectronicSheetParam editParam) {
        iShopDeliveryElectronicSheetService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 电子面单删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    @Description("删除电子面单")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        iShopDeliveryElectronicSheetService.del(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Description("删除电子面单")
    public Result<?> delElectronicSheet(@PathVariable("id") Integer id) {
        iShopDeliveryElectronicSheetService.del(id);
        return Result.success();
    }

    @PostMapping("/config")
    @Description("设置电子面单配置")
    public Result<?> setConfig(@RequestBody Object obj) {
        iShopDeliveryElectronicSheetService.setElectronicSheetConfig(obj);
        return Result.success();
    }

    /**
     * 电子面单 获取设置
     *
     * @return
     */
    @GetMapping("/config")
    @Description("获取电子面单配置")
    public Result<?> getConfig() {
        return Result.success(iShopDeliveryElectronicSheetService.getElectronicSheetConfig());
    }

    @GetMapping("/paytype")
    @Description("获取支付方式")
    public Result<?> paytype() {
        return Result.success(iShopDeliveryElectronicSheetService.getPayType());
    }

    @PutMapping("/setDefault/{id}")
    @Description("设置默认电子面单")
    public Result<?> setDefault(@PathVariable("id") Integer id) {
        iShopDeliveryElectronicSheetService.setDefault(id);
        return Result.success();
    }

    @PostMapping("/print")
    @Description("打印电子面单")
    public Result<?> printElectronicSheet(@RequestBody ShopDeliveryPrintElectronicSheetParam param) {
        List<ElectronicSheetPrintVo> results = iShopDeliveryElectronicSheetService.printElectronicSheet(param);
        if (CollectionUtil.size(results) != 1) {
            return Result.success(results);
        }

        ElectronicSheetPrintVo result = results.get(0);
        if (result.isSuccess()) {
            return Result.success(results);
        }

        return Result.fail(result.getReason());
    }
}
