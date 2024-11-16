package com.niu.shop.controller.adminapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryShippingTemplateParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryShippingTemplateSearchParam;
import com.niu.shop.service.admin.delivery.IShopDeliveryShippingTemplateService;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryShippingTemplateInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryShippingTemplateListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 运费模板控制器
 */

@RestController
@RequestMapping("adminapi/shop/shipping/template")
public class ShippingTemplateController {

    @Resource
    IShopDeliveryShippingTemplateService iShopDeliveryShippingTemplateService;

    /**
     * 运费模板分页列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopDeliveryShippingTemplateListVo>>
     */
    @GetMapping("")
    public Result<PageResult<ShopDeliveryShippingTemplateListVo>> page(@Validated PageParam pageParam, @Validated ShopDeliveryShippingTemplateSearchParam searchParam) {
        PageResult<ShopDeliveryShippingTemplateListVo> list = iShopDeliveryShippingTemplateService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 运费模板列表
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopDeliveryShippingTemplateListVo>>
     */
    @GetMapping("/list")
    public Result<List<ShopDeliveryShippingTemplateListVo>> list(@Validated ShopDeliveryShippingTemplateSearchParam searchParam) {
        List<ShopDeliveryShippingTemplateListVo> list = iShopDeliveryShippingTemplateService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 运费模板详情
     * @param id 主键ID
     * @return Result<ShopDeliveryShippingTemplateInfoVo>
     */
    @GetMapping("/{template_id}")
    public Result<ShopDeliveryShippingTemplateInfoVo> info(@PathVariable("template_id") Integer id) {
        ShopDeliveryShippingTemplateInfoVo info = iShopDeliveryShippingTemplateService.info(id);
        return Result.success(info);
    }

    /**
     * 运费模板添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody ShopDeliveryShippingTemplateParam addParam) {
        iShopDeliveryShippingTemplateService.add(addParam);
        return Result.success();
    }

    /**
     * 运费模板编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{template_id}")
    public Result<Object> edit(@PathVariable("template_id") Integer id, @Validated @RequestBody ShopDeliveryShippingTemplateParam editParam) {
        iShopDeliveryShippingTemplateService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 运费模板删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{template_id}")
    public Result<Object> del(@PathVariable("template_id") Integer id) {
        iShopDeliveryShippingTemplateService.del(id);
        return Result.success();
    }

}
