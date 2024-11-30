package com.niu.core.listener.pay;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.pay.PaySuccessEvent;
import com.niu.core.service.core.site.ICoreSiteAccountService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 支付成功
 */
@EventListen("core")
@Component
public class PaySuccessListener extends AbstractListener {

    @Resource
    ICoreSiteAccountService coreSiteAccountService;

    public void handleEvent(PaySuccessEvent event) {
        coreSiteAccountService.addPayLog(event.getPay());
    }
}
