package com.niu.core.controller.adminapi.pay;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.pay.IPayService;
import com.niu.core.service.admin.pay.param.GetFriendspayInfoByTradeParam;
import com.niu.core.service.admin.pay.param.PayParam;
import com.niu.core.service.admin.pay.param.PaySearchParam;
import com.niu.core.service.admin.pay.vo.PayInfoVo;
import com.niu.core.service.admin.pay.vo.PayListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jdk.jfr.Description;

/**
 * 支付记录控制器
 */

@RestController
@RequestMapping("adminapi/pay")
@SaCheckLogin
@Description("支付记录")
public class PayController {

    @Resource
    IPayService payService;

    @Description("获取交易支付信息")
    @GetMapping("/friendspay/info/{trade_type}/{trade_id}/{channel}")
    public Result<?> getFriendspayInfoByTrade(@PathVariable("trade_type") String tradeType, @PathVariable("trade_id") Integer tradeId, @PathVariable("channel") String channel) {
        GetFriendspayInfoByTradeParam param = new GetFriendspayInfoByTradeParam();
        param.setTradeType(tradeType);
        param.setTradeId(tradeId);
        param.setChannel(channel);
        return Result.success(payService.getFriendspayInfoByTrade(param));
    }

    @Description("支付类型列表")
    @GetMapping("/type/list")
    public Result<?> payTypeList() {
        return Result.success(payService.getPayTypeList());
    }

    @Description("发起支付")
    @PostMapping
    public Result<?> pay(@RequestBody com.niu.core.common.loader.pay.param.PayParam param) {
        return Result.success(payService.pay(param));
    }

}
