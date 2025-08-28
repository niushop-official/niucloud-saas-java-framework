package com.niu.shop.controller.adminapi.delivery;

import jdk.jfr.Description;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryCompanyParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryCompanySearchParam;
import com.niu.shop.service.admin.delivery.IShopDeliveryCompanyService;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryCompanyInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryCompanyListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 物流公司控制器
 */

@RestController
@RequestMapping("adminapi/shop/delivery/company")
@Description("物流公司管理")
public class CompanyController {

    @Resource
    IShopDeliveryCompanyService deliveryCompanyService;

    /**
     * 物流公司列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopDeliveryCompanyListVo>>
     */
    @GetMapping("")
    @Description("物流公司分页列表")
    public Result<PageResult<ShopDeliveryCompanyListVo>> page(@Validated PageParam pageParam, @Validated ShopDeliveryCompanySearchParam searchParam) {
        PageResult<ShopDeliveryCompanyListVo> list = deliveryCompanyService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 物流公司列表
     * @param searchParam 搜索条件
     * @return Result<List<ShopDeliveryCompanyListVo>>
     */
    @GetMapping("/list")
    @Description("物流公司列表")
    public Result<List<ShopDeliveryCompanyListVo>> list(@Validated ShopDeliveryCompanySearchParam searchParam) {
        List<ShopDeliveryCompanyListVo> list = deliveryCompanyService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 物流公司详情
     * @param id 主键ID
     * @return Result<ShopDeliveryCompanyInfoVo>
     */
    @GetMapping("/{id}")
    @Description("物流公司详情")
    public Result<ShopDeliveryCompanyInfoVo> info(@PathVariable("id") Integer id) {
        ShopDeliveryCompanyInfoVo info = deliveryCompanyService.info(id);
        return Result.success(info);
    }

    /**
     * 物流公司添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    @Description("添加物流公司")
    public Result<Object> add(@Validated @RequestBody ShopDeliveryCompanyParam addParam) {
        deliveryCompanyService.add(addParam);
        return Result.success();
    }

    /**
     * 物流公司编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    @Description("编辑物流公司")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopDeliveryCompanyParam editParam) {
        deliveryCompanyService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 物流公司删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    @Description("删除物流公司")
    public Result<Object> del(@PathVariable("id") Integer id) {
        deliveryCompanyService.del(id);
        return Result.success();
    }

}
