package com.niu.recharge.service.core.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.recharge.entity.RechargeOrder;
import com.niu.recharge.entity.RechargeOrderItem;
import com.niu.recharge.enums.OrderStatusEnum;
import com.niu.recharge.mapper.RechargeOrderItemMapper;
import com.niu.recharge.mapper.RechargeOrderMapper;
import com.niu.recharge.service.core.ICoreRechargeOrderService;
import com.niu.recharge.service.core.param.RechargeOrderCreateParam;
import com.niu.recharge.service.core.vo.RechargeOrderCreateVo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class CoreRechargeOrderServiceImpl implements ICoreRechargeOrderService {

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    RechargeOrderItemMapper rechargeOrderItemMapper;

    @Resource
    ICorePayService corePayService;

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    @Resource
    ICoreNoticeService coreNoticeService;

    /**
     *
     * @param param
     * @return
     */
    @Override
    @Transactional
    public RechargeOrderCreateVo create(RechargeOrderCreateParam param) {
        try {
            RechargeOrderCreateVo vo = new RechargeOrderCreateVo();
            vo.setTradeType("recharge");

            RechargeOrder order = new RechargeOrder();
            order.setOrderNo(createOrderNo());
            order.setOrderStatus(OrderStatusEnum.WAIT_PAY.getStatus());
            order.setOrderFrom(RequestUtils.channel());
            order.setSiteId(param.getSiteId());
            order.setMemberId(param.getMemberId());
            order.setIp(RequestUtils.ip());
            order.setMemberMessage(ObjectUtil.defaultIfNull(param.getMemberMessage(), ""));
            order.setOrderItemMoney(param.getRechargeMoney());
            order.setOrderMoney(param.getRechargeMoney());
            order.setOrderType(vo.getTradeType());
            order.setCreateTime(System.currentTimeMillis() / 1000);
            this.rechargeOrderMapper.insert(order);

            RechargeOrderItem orderItem = new RechargeOrderItem();
            orderItem.setOrderId(order.getOrderId());
            orderItem.setSiteId(param.getSiteId());
            orderItem.setMemberId(param.getMemberId());
            orderItem.setItemType(vo.getTradeType());
            orderItem.setItemName("会员充值");
            orderItem.setItemImage("/static/image/icon.png");
            orderItem.setNum(1);
            orderItem.setPrice(param.getRechargeMoney());
            orderItem.setItemMoney(param.getRechargeMoney());
            orderItem.setCreateTime(System.currentTimeMillis() / 1000);
            this.rechargeOrderItemMapper.insert(orderItem);

            vo.setTradeId(order.getOrderId());
            return vo;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 创建交易号
     *
     * @return
     */
    private String createOrderNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }

    /**
     * 关闭支付
     *
     * @param orderId
     */
    @Override
    @Transactional
    public void close(Integer orderId) {
        RechargeOrder order = orderInfo(orderId);
        if (order == null) return;

        if (order.getOrderStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus())) {
            corePayService.closeByTrade(order.getSiteId(), "recharge", orderId);
        }

        order.setOrderStatus(OrderStatusEnum.CLOSE.getStatus());
        this.rechargeOrderMapper.updateById(order);
    }

    /**
     * 获取订单信息
     *
     * @param orderId
     * @return
     */
    public RechargeOrder orderInfo(Integer orderId) {
        return this.rechargeOrderMapper.selectById(orderId);
    }

    /**
     * 订单支付
     *
     * @param pay
     */
    @Transactional
    public void pay(Pay pay) {
        try {
            RechargeOrder order = this.orderInfo(pay.getTradeId());
            if (order == null) throw new CommonException("订单不存在");

            order.setPayTime(System.currentTimeMillis() / 1000);
            order.setOrderStatus(OrderStatusEnum.FINISH.getStatus());
            order.setIsEnableRefund(1);
            order.setOutTradeNo(pay.getOutTradeNo());
            this.rechargeOrderMapper.updateById(order);

            // 增加会员余额
            coreMemberAccountService.addLog(order.getSiteId(), order.getMemberId(), AccountTypeEnum.BALANCE.getType(), order.getOrderMoney().doubleValue(), "recharge", "会员充值", order.getOrderId().toString());

            // 发送消息
            sendRechargeSuccessNotice(order);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("充值订单支付异常");
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 发送充值成功消息
     * @param order
     */
    @Async
    private void sendRechargeSuccessNotice(RechargeOrder order) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("order", order);
        coreNoticeService.send(order.getSiteId(), "recharge_success", param);
    }

}
