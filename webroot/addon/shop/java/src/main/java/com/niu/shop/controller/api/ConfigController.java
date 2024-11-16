package com.niu.shop.controller.api;

import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/shop")
public class ConfigController {

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    /**
     * 获取发票配置
     *
     * @return
     */
    @GetMapping("/config/invoice")
    public Result getInvoiceConfig() {
        return Result.success(coreOrderConfigService.invoice(RequestUtils.siteId()));
    }

    /**
     * 获取评价配置
     *
     * @return
     */
    @GetMapping("/goods/evaluate/config")
    public Result getEvaluateConfig() {
        return Result.success(coreOrderConfigService.getEvaluateConfig(RequestUtils.siteId()));
    }
}
