package com.niu.shop.controller.adminapi.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.admin.order.IShopInvoiceService;
import com.niu.shop.service.admin.order.param.ShopInvoiceParam;
import com.niu.shop.service.admin.order.param.ShopInvoiceSearchParam;
import com.niu.shop.service.admin.order.vo.ShopInvoiceInfoVo;
import com.niu.shop.service.admin.order.vo.ShopInvoiceListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 发票控制器
 */

@RestController
@RequestMapping("adminapi/shop")
public class ShopInvoiceController {

    @Resource
    IShopInvoiceService shopInvoiceService;

    /**
     * 发票列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < ShopInvoiceListVo>>
     */
    @GetMapping("/invoice")
    public Result<PageResult<ShopInvoiceListVo>> lists(@Validated PageParam pageParam, ShopInvoiceSearchParam searchParam) {
        PageResult<ShopInvoiceListVo> list = shopInvoiceService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 发票详情
     *
     * @return
     */
    @GetMapping("/invoice/{id}")
    public Result<ShopInvoiceInfoVo> info(@Validated @PathVariable("id") Integer id) {
        ShopInvoiceInfoVo info = shopInvoiceService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 发票编辑
     *
     * @param shopInvoiceParam 开票参数
     * @return Result<Object>
     */
    @PutMapping("/invoice/{id}")
    public Result<Object> invoicing(@PathVariable Integer id, @Validated @RequestBody ShopInvoiceParam shopInvoiceParam) {
        shopInvoiceService.invoicing(id, shopInvoiceParam);
        return Result.success();
    }


}
