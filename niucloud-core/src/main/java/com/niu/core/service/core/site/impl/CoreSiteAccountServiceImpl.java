package com.niu.core.service.core.site.impl;

import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.entity.site.SiteAccountLog;
import com.niu.core.mapper.site.SiteAccountLogMapper;
import com.niu.core.service.core.site.ICoreSiteAccountService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;

@Service
public class CoreSiteAccountServiceImpl implements ICoreSiteAccountService {

    @Resource
    SiteAccountLogMapper siteAccountLogMapper;

    /**
     * 添加支付账单
     * @param pay
     */
    @Override
    public Integer addPayLog(Pay pay) {
        SiteAccountLog model = new SiteAccountLog();
        model.setSiteId(pay.getSiteId());
        model.setType("pay");
        model.setMoney(pay.getMoney());
        model.setTradeNo(pay.getOutTradeNo());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        siteAccountLogMapper.insert(model);
        return model.getId();
    }

    /**
     * 添加退款账单记录
     * @param refund
     */
    @Override
    public Integer addRefundLog(PayRefund refund) {
        SiteAccountLog model = new SiteAccountLog();
        model.setSiteId(refund.getSiteId());
        model.setType("refund");
        model.setMoney(refund.getMoney().multiply(new BigDecimal("-1")));
        model.setTradeNo(refund.getRefundNo());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        siteAccountLogMapper.insert(model);
        return model.getId();
    }

    /**
     * 添加转账账单记录
     * @param transfer
     */
    @Override
    public Integer addTransferLog(PayTransfer transfer) {
        SiteAccountLog model = new SiteAccountLog();
        model.setSiteId(transfer.getSiteId());
        model.setType("transfer");
        model.setMoney(transfer.getMoney().multiply(new BigDecimal("-1")));
        model.setTradeNo(transfer.getTransferNo());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        siteAccountLogMapper.insert(model);
        return model.getId();
    }
}
