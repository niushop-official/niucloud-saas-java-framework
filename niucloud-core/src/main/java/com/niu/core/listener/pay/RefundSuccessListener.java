package com.niu.core.listener.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.refund.RefundSuccessEvent;
import com.niu.core.service.core.site.ICoreSiteAccountService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 退款成功
 */
@EventListen("core")
@Component
public class RefundSuccessListener extends AbstractListener {

    @Resource
    ICoreSiteAccountService coreSiteAccountService;

    public void handleEvent(RefundSuccessEvent event) {
        coreSiteAccountService.addRefundLog(event.getRefund());
    }
}
