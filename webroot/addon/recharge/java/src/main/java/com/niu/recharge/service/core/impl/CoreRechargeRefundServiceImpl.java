package com.niu.recharge.service.core.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.ICoreRefundService;
import com.niu.core.service.core.pay.param.PayRefundCreateParam;
import com.niu.core.service.core.pay.param.PayRefundTransferParam;
import com.niu.recharge.entity.RechargeOrder;
import com.niu.recharge.entity.RechargeOrderItem;
import com.niu.recharge.entity.RechargeOrderItemRefund;
import com.niu.recharge.enums.OrderStatusEnum;
import com.niu.recharge.enums.RefundStatusEnum;
import com.niu.recharge.mapper.RechargeOrderItemMapper;
import com.niu.recharge.mapper.RechargeOrderItemRefundMapper;
import com.niu.recharge.mapper.RechargeOrderMapper;
import com.niu.recharge.service.core.ICoreRechargeOrderService;
import com.niu.recharge.service.core.ICoreRechargeRefundService;
import com.niu.recharge.service.core.param.RechargeOrderCreateParam;
import com.niu.recharge.service.core.vo.RechargeOrderCreateVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class CoreRechargeRefundServiceImpl implements ICoreRechargeRefundService {

    @Resource
    RechargeOrderItemMapper rechargeOrderItemMapper;

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreRefundService coreRefundService;

    @Resource
    RechargeOrderItemRefundMapper rechargeOrderItemRefundMapper;

    @Resource
    ICoreRechargeOrderService coreRechargeOrderService;

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    /**
     *
     * @return
     */
    @Override
    @Transactional
    public void create(Integer orderId) {
        RechargeOrder order = rechargeOrderMapper.selectOne(new QueryWrapper<RechargeOrder>().eq("order_id", orderId));
        if (order == null) throw new CommonException("订单不存在");
        if (!order.getIsEnableRefund().equals(1)) throw new CommonException("该订单不允许退款");
        if (!order.getOrderStatus().equals(OrderStatusEnum.FINISH.getStatus())) throw new CommonException("订单尚未支付不能进行退款");

        RechargeOrderItem orderItem = rechargeOrderItemMapper.selectOne(new QueryWrapper<RechargeOrderItem>().eq("order_id", orderId));
        if (!orderItem.getRefundStatus().equals(0)) throw new CommonException("订单已申请退款");

        Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("balance").eq("member_id", order.getMemberId()));
        if (member == null) throw new CommonException("会员不存在");
        if (member.equals(0)) throw new CommonException("会员账户金额为0不允许进行退款");

        try {
            BigDecimal money = member.getBalance().compareTo(order.getOrderMoney()) == 1 ? order.getOrderMoney() : member.getBalance();

            // 创建支付单据
            PayRefundCreateParam refundParam = new PayRefundCreateParam();
            refundParam.setOutTradeNo(order.getOutTradeNo());
            refundParam.setSiteId(order.getSiteId());
            refundParam.setTradeType("recharge");
            refundParam.setTradeId(orderId.toString());
            refundParam.setMoney(money);
            String refundNo = coreRefundService.create(refundParam);

            // 添加退款记录
            RechargeOrderItemRefund model = new RechargeOrderItemRefund();
            model.setRefundNo(refundNo);
            model.setOrderId(orderId);
            model.setOrderItemId(orderItem.getOrderItemId());
            model.setNum(orderItem.getNum());
            model.setMoney(money);
            model.setItemType(orderItem.getItemType());
            model.setSiteId(order.getSiteId());
            model.setMemberId(order.getMemberId());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            model.setStatus(RefundStatusEnum.REFUNDING.getStatus());
            rechargeOrderItemRefundMapper.insert(model);

            order.setRefundStatus(RefundStatusEnum.REFUNDING.getStatus());
            rechargeOrderMapper.updateById(order);

            orderItem.setRefundNo(refundNo);
            orderItem.setRefundStatus(RefundStatusEnum.REFUNDING.getStatus());
            rechargeOrderItemMapper.updateById(orderItem);

            this.refund(model);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Transactional
    public void refund(RechargeOrderItemRefund refund) {
        // 扣除账户
        coreMemberAccountService.addLog(refund.getSiteId(), refund.getMemberId(), AccountTypeEnum.BALANCE.getType(), refund.getMoney().negate().doubleValue(), "recharge_refund", "充值订单退款", refund.getOrderId().toString());

        PayRefundTransferParam param = new PayRefundTransferParam();
        param.setSiteId(refund.getSiteId());
        param.setRefundNo(refund.getRefundNo());
        coreRefundService.refund(param);
    }

    /**
     * 退款完成
     *
     * @param refundNo
     */
    @Transactional
    public void refundComplete(String refundNo) {
        RechargeOrderItemRefund refund = rechargeOrderItemRefundMapper.selectOne(new QueryWrapper<RechargeOrderItemRefund>().eq("refund_no", refundNo));
        if (refund == null) return;
        if (!refund.getStatus().equals(RefundStatusEnum.REFUNDING.getStatus())) return;

        refund.setStatus(RefundStatusEnum.REFUND_COMPLETED.getStatus());
        rechargeOrderItemRefundMapper.updateById(refund);

        RechargeOrder order = new RechargeOrder();
        order.setOrderId(refund.getOrderId());
        order.setRefundStatus(RefundStatusEnum.REFUND_COMPLETED.getStatus());
        rechargeOrderMapper.updateById(order);

        RechargeOrderItem orderItem = new RechargeOrderItem();
        orderItem.setOrderItemId(refund.getOrderItemId());
        orderItem.setRefundStatus(RefundStatusEnum.REFUND_COMPLETED.getStatus());
        rechargeOrderItemMapper.updateById(orderItem);

        coreRechargeOrderService.close(refund.getOrderId());
    }
}
