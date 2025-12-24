package com.niu.shop.controller.api.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.shop.service.api.order.IOrderService;
import com.niu.shop.service.api.order.IShopInvoiceService;
import com.niu.shop.service.api.order.param.ShopInvoiceAddParam;
import com.niu.shop.service.api.order.param.ShopInvoiceEditParam;
import com.niu.shop.service.api.order.vo.ShopInvoiceListVo;
import com.niu.shop.service.api.order.vo.ShopInvoiceInfoVo;
import com.niu.shop.service.api.order.param.ShopInvoiceSearchParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;

/**
 * 发票控制器
 */
@RestController
@RequestMapping("api/shop/order/invoice")
@SaCheckLogin
public class InvoiceController {

    @Resource
    private IShopInvoiceService invoiceService;

    @Resource
    private IOrderService orderService;

    /**
     * 获取订单发票列表
     *
     * @param pageParam
     * @return
     */
    @GetMapping("/list")
    public Result<PageResult<ShopInvoiceListVo>> list(@Validated PageParam pageParam, ShopInvoiceSearchParam searchParam) {
        return Result.success(invoiceService.getPage(pageParam, searchParam));
    }

    /**
     * 获取订单发票详情
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<ShopInvoiceInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(invoiceService.getInfo(id));
    }

    /**
     * 申请发票
     *
     * @param param
     * @return
     */
    @PostMapping("/apply")
    public Result<?> apply(@Validated @RequestBody ShopInvoiceAddParam param) {
        invoiceService.apply(param);
        return Result.success();
    }

    /**
     * 编辑发票
     *
     * @param param
     * @return
     */
    @PostMapping("/edit/{id}")
    public Result<?> edit(@PathVariable("id") Integer id, @Validated @RequestBody ShopInvoiceEditParam param) {
        param.setId(id);
        invoiceService.edit(param);
        return Result.success();
    }

    /**
     * 取消发票
     *
     * @param id
     * @return
     */
    @DeleteMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable("id") Integer id) {
        invoiceService.cancel(id);
        return Result.success();
    }

    /**
     * 计算发票金额
     */
    @PostMapping("/calculate")
    public Result<?> calculate(@RequestBody Map<String, List<Integer>> param) {
        List<Integer> tradeIds = param.getOrDefault("trade_ids", List.of());
        return Result.success(orderService.calculateInvoiceMoney(tradeIds));
    }

}