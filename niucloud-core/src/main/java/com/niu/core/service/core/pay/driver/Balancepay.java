package com.niu.core.service.core.pay.driver;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.loader.pay.BasePay;
import com.niu.core.common.loader.pay.param.*;
import com.niu.core.component.context.SpringContext;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.member.MemberAccountChangeTypeEnum;
import com.niu.core.enums.pay.OnliepayStatusEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.pay.RefundTransferStatusEnum;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.ICoreRefundService;
import com.niu.core.service.core.pay.ICoreTransferService;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class Balancepay extends BasePay {

    public ICorePayService corePayService() {
        return SpringContext.bean(ICorePayService.class);
    }

    public ICoreRefundService coreRefundService() {
        return SpringContext.bean(ICoreRefundService.class);
    }

    public ICoreMemberAccountService coreMemberAccountService() {
        return SpringContext.bean(ICoreMemberAccountService.class);
    }

    public BasePay init(JSONObject config) {
        return this;
    }

    /**
     * 支付
     * @param param
     */
    public Object pay(PayParam param) {
        // 扣除账户余额
        Pay pay = param.getPay();
        String memo = MemberAccountChangeTypeEnum.getType(AccountTypeEnum.BALANCE.getType()).getByPath("order.name", String.class);
        coreMemberAccountService().addLog(param.getSiteId(), param.getBuyerId(), AccountTypeEnum.BALANCE.getType(), pay.getMoney().negate().doubleValue(), "order", memo, pay.getOutTradeNo());

        PayNotifyParam notifyParam = new PayNotifyParam();
        notifyParam.setSiteId(param.getSiteId());
        notifyParam.setOutTradeNo(pay.getOutTradeNo());
        notifyParam.setType("balancepay");
        notifyParam.setPayStatus(OnliepayStatusEnum.SUCCESS);
        corePayService().payNotify(notifyParam);

        return null;
    }

    /**
     * 扫码支付
     */
    public void scan() {

    }

    /**
     * 关闭支付
     */
    public void close(Pay pay) {

    }

    /**
     * 异步回调
     * @param request
     * @param response
     */
    public Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    /**
     * 转账
     */
    public void transfer(TransferParam param) {
    }

    /**
     * 退款
     */
    public void refund(RefundParam param) {
        String memo = MemberAccountChangeTypeEnum.getType(AccountTypeEnum.BALANCE.getType()).getByPath("order_refund.name", String.class);
        coreMemberAccountService().addLog(param.getSiteId(), param.getMemberId(), AccountTypeEnum.BALANCE.getType(), param.getMoney().negate().doubleValue(), "order_refund", memo, param.getRefundNo());

        RefundNotifyParam notifyParam = new RefundNotifyParam();
        BeanUtil.copyProperties(param, notifyParam);
        notifyParam.setType("balance");
        notifyParam.setRefundStatus(RefundTransferStatusEnum.SUCCESS);
        coreRefundService().refundNotify(notifyParam);
    }

    /**
     * 过滤支付方式 通过交易类型
     * @param tradeType
     * @return
     */
    public PayTypeVo filterPayTypeByTradeType(String tradeType) {
        PayTypeVo vo = JSONUtil.toBean(PayTypeEnum.getType().getJSONObject("balancepay"), PayTypeVo.class);
        if (tradeType.equals("recharge")) return null;
        return vo;
    }
}
