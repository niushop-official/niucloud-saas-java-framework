package com.niu.core.controller.api.pay;

import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.api.pay.IPayService;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class PayController {

    @Resource
    IPayService payService;

    /**
     * 支付
     *
     * @param param
     * @return
     */
    @PostMapping("/pay")
    public Result<?> pay(@Validated @RequestBody PayParam param) {
        return Result.success(payService.pay(param));
    }

    /**
     * 支付异步通知
     *
     * @param siteId
     * @param channel
     * @param type
     * @param action
     * @param request
     * @param response
     * @return
     */
    @SaIgnore
    @RequestMapping("/pay/notify/{site_id}/{channel}/{type}/{action}")
    public Object notify(@PathVariable(name = "site_id") Integer siteId, @PathVariable(name = "channel") String channel, @PathVariable(name = "type") String type, @PathVariable(name = "action") String action, HttpServletRequest request, HttpServletResponse response) {
        RequestUtils.setSiteId(siteId);
        PayAsyncNotifyParam param = new PayAsyncNotifyParam();
        param.setSiteId(siteId);
        param.setType(type);
        param.setAction(action);
        param.setChannel(channel);
        return payService.asyncNotify(param, request, response);
    }

    /**
     * 支付
     *
     * @return
     */
    @GetMapping("/pay/info/{trade_type}/{trade_id}")
    public Result<?> info(@PathVariable("trade_type") String tradeType, @PathVariable("trade_id") Integer tradeId) {
        return Result.success(payService.getInfoByTrade(tradeType, tradeId));
    }

}