package com.niu.recharge.service.core.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.NoticeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.recharge.entity.Recharge;
import com.niu.recharge.entity.RechargeOrder;
import com.niu.recharge.entity.RechargeOrderItem;
import com.niu.recharge.enums.OrderStatusEnum;
import com.niu.recharge.event.RechargeAfterEvent;
import com.niu.recharge.mapper.RechargeMapper;
import com.niu.recharge.mapper.RechargeOrderItemMapper;
import com.niu.recharge.mapper.RechargeOrderMapper;
import com.niu.recharge.service.core.ICoreRechargeOrderService;
import com.niu.recharge.service.core.param.RechargeOrderCreateParam;
import com.niu.recharge.service.core.vo.RechargeOrderCreateVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
    RechargeMapper rechargeMapper;

    /**
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
            order.setOrderDiscountMoney(BigDecimal.ZERO);
            order.setOrderType(vo.getTradeType());
            order.setCreateTime(System.currentTimeMillis() / 1000);
            this.rechargeOrderMapper.insert(order);

            RechargeOrderItem orderItem = new RechargeOrderItem();
            orderItem.setOrderId(order.getOrderId());
            orderItem.setItemId(param.getRechargeId());
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
            log.info("pay order:{}", JSONUtil.toJsonStr(order));
            if (order == null) throw new CommonException("订单不存在");

            order.setPayTime(System.currentTimeMillis() / 1000);
            order.setOrderStatus(OrderStatusEnum.FINISH.getStatus());
            order.setIsEnableRefund(1);
            order.setOutTradeNo(pay.getOutTradeNo());
            this.rechargeOrderMapper.updateById(order);

            QueryWrapper<RechargeOrderItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", pay.getTradeId());
            List<RechargeOrderItem> items = rechargeOrderItemMapper.selectList(queryWrapper);
            if (ObjectUtil.isEmpty(items)) {
                return;
            }

            Recharge recharge = rechargeMapper.selectById(items.get(0).getItemId());
            if (ObjectUtil.isEmpty(recharge)) {
                return;
            }

            recharge.setSaleNum(recharge.getSaleNum() + 1);
            rechargeMapper.updateById(recharge);

            // 增加会员余额
            if (recharge.getFaceValue().compareTo(BigDecimal.ZERO) > 0) {
                coreMemberAccountService.addLog(order.getSiteId(), order.getMemberId(), AccountTypeEnum.BALANCE.getType(), recharge.getFaceValue().doubleValue(), "recharge", "会员充值", order.getOrderId().toString());
            }

            // 会员充值发放积分开始
            if (recharge.getPoint() > 0) {
                coreMemberAccountService.addLog(order.getSiteId(), order.getMemberId(), AccountTypeEnum.POINT.getType(), recharge.getPoint().doubleValue(), "recharge", "会员充值", order.getOrderId().toString());
            }

            // 会员充值发放成长值开始
            if (recharge.getGrowth() > 0) {
                coreMemberAccountService.addLog(order.getSiteId(), order.getMemberId(), AccountTypeEnum.GROWTH.getType(), recharge.getGrowth().doubleValue(), "recharge", "会员充值", order.getOrderId().toString());
            }

            if (ObjectUtil.isNotEmpty(recharge.getGiftJson())) {
                EventAndSubscribeOfPublisher.publishAll(new RechargeAfterEvent(order.getMemberId(), recharge.getGiftJson()).setSiteId(order.getSiteId()));
            }

            // 发送消息
            sendRechargeSuccessNotice(order);
            log.info("pay success");
        } catch (Exception e) {
            log.info("pay fail: {}", e.getMessage());
            System.out.println("充值订单支付异常");
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 发送充值成功消息
     *
     * @param order
     */
    @Async
    protected void sendRechargeSuccessNotice(RechargeOrder order) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("order", order);
        NoticeUtils.send(order.getSiteId(), "recharge_success", param);
    }

}
