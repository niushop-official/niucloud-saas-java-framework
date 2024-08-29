package com.niu.core.service.core.pay.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.pay.param.RefundNotifyParam;
import com.niu.core.common.loader.pay.param.RefundParam;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.enums.pay.PayStatusEnum;
import com.niu.core.enums.pay.RefundStatusEnum;
import com.niu.core.enums.pay.RefundTransferStatusEnum;
import com.niu.core.enums.pay.RefundTypeEnum;
import com.niu.core.event.pay.PayCloseEvent;
import com.niu.core.event.refund.RefundFailEvent;
import com.niu.core.event.refund.RefundSuccessEvent;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.pay.PayRefundMapper;
import com.niu.core.mapper.pay.PayTransferMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.ICoreRefundService;
import com.niu.core.service.core.pay.param.PayRefundCreateParam;
import com.niu.core.service.core.pay.param.PayRefundTransferParam;
import com.niu.core.service.core.pay.param.PayTransferParam;
import com.niu.core.service.core.pay.param.PayTransferSearchParam;
import com.niu.core.service.core.pay.vo.PayTransferInfoVo;
import com.niu.core.service.core.pay.vo.PayTransferListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;


/**
 * 转账实现
 */
@Service
public class CoreRefundServiceImpl implements ICoreRefundService {
        
    @Resource
    PayRefundMapper payRefundMapper;

    @Resource
    PayMapper payMapper;

    @Resource
    ICorePayService corePayService;

    /**
     * 创建退款单据
     *
     * @param param
     * @return
     */
    public String create(@Validated PayRefundCreateParam param) {
        Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("site_id", param.getSiteId()).eq("out_trade_no", param.getOutTradeNo()));
        if (pay == null) throw new CommonException("无效的支付交易号");
        if (param.getMoney().compareTo(new BigDecimal(0)) <= 0) throw new CommonException("退款金额需大于0元");

        PayRefund model = new PayRefund();
        model.setSiteId(param.getSiteId());
        model.setMoney(param.getMoney());
        model.setType(pay.getType());
        model.setChannel(pay.getChannel());
        model.setOutTradeNo(pay.getOutTradeNo());
        model.setRefundNo(createRefundNo());
        model.setReason(param.getReason());
        model.setTradeType(param.getTradeType());
        model.setTradeId(param.getTradeId());
        model.setStatus(RefundStatusEnum.WAIT.getStatus());
        model.setCreateTime(System.currentTimeMillis() / 1000);

        payRefundMapper.insert(model);
        return model.getRefundNo();
    }

    /**
     * 创建交易号
     *
     * @return
     */
    private String createRefundNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }

    /**
     * 发起退款
     *
     */
    public void refund(PayRefundTransferParam param) {
        PayRefund refund = this.findPayInfoByOutTradeNo(param.getSiteId(), param.getRefundNo());
        if (refund == null) throw new CommonException("无效的退款单据");

        Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("site_id", refund.getSiteId()).eq("out_trade_no", refund.getOutTradeNo()));
        if (pay == null) throw new CommonException("无效的支付交易号");

        if (param.getRefundType().equals(RefundTypeEnum.BACK.getKey())) {
            RefundParam refundParam = new RefundParam();
            refundParam.setSiteId(param.getSiteId());
            refundParam.setRefundNo(param.getRefundNo());
            refundParam.setOutTradeNo(refund.getOutTradeNo());
            refundParam.setMoney(refund.getMoney());
            refundParam.setTotal(pay.getMoney());
            refundParam.setNotifyUrl(corePayService.buildNotifyUrl(param.getSiteId(), refund.getChannel(), refund.getType(), "refund"));
            corePayService.driver(param.getSiteId(), refund.getChannel(), refund.getType()).refund(refundParam);
        } else if (param.getRefundType().equals(RefundTypeEnum.OFFLINE.getKey())) {
            refund.setVoucher(param.getVoucher());
            this.payRefundMapper.updateById(refund);
            RefundNotifyParam notifyParam = new RefundNotifyParam();
            notifyParam.setRefundNo(param.getRefundNo());
            notifyParam.setSiteId(param.getSiteId());
            notifyParam.setOutTradeNo(refund.getOutTradeNo());
            notifyParam.setType(param.getRefundType());
            notifyParam.setRefundStatus(RefundTransferStatusEnum.SUCCESS);
            refundNotify(notifyParam);
        }
    }

    /**
     * 通过交易流水号查询退款单据
     *
     * @param siteId
     * @param refundNo
     */
    public PayRefund findPayInfoByOutTradeNo(Integer siteId, String refundNo) {
        return payRefundMapper.selectOne(new QueryWrapper<PayRefund>()
                .eq("site_id", siteId)
                .eq("refund_no", refundNo)
        );
    }

    /**
     * 退款结果通知
     *
     * @param param
     */
    @Transactional
    public void refundNotify(RefundNotifyParam param) {
        PayRefund refund = findPayInfoByOutTradeNo(param.getSiteId(), param.getRefundNo());
        if (refund == null) throw new CommonException("退款单据不存在");
        if (!refund.getStatus().equals(RefundStatusEnum.WAIT.getStatus()) && !refund.getStatus().equals(RefundStatusEnum.DEALING.getStatus())) {
            throw new CommonException("退款状态已发生变化");
        }

        refund.setRefundType(param.getType());

        // 退款成功
        if (param.getRefundStatus().getStatus().equals(RefundTransferStatusEnum.SUCCESS.getStatus())) {
            this.refundSuccess(param.getSiteId(), refund);
        }
        // 退款失败
        if (param.getRefundStatus().getStatus().equals(RefundTransferStatusEnum.ABNORMAL.getStatus())) {
            this.refundFail(param.getSiteId(), refund);
        }
    }

    public void refundSuccess(Integer siteId, PayRefund refund) {
        refund.setStatus(RefundStatusEnum.SUCCESS.getStatus());
        refund.setRefundTime(System.currentTimeMillis() / 1000);
        payRefundMapper.updateById(refund);

        RefundSuccessEvent event = new RefundSuccessEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setTradeType(refund.getTradeType());
        event.setTradeId(refund.getTradeId());
        event.setRefund(refund);
        event.setName("RefundSuccessEvent");
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    public void refundFail(Integer siteId, PayRefund refund) {
        refund.setStatus(RefundStatusEnum.FAIL.getStatus());
        payRefundMapper.updateById(refund);

        RefundFailEvent event = new RefundFailEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setTradeType(refund.getTradeType());
        event.setTradeId(refund.getTradeId());
        event.setRefund(refund);
        event.setName("RefundFailEvent");
        EventAndSubscribeOfPublisher.publishAll(event);
    }

}
