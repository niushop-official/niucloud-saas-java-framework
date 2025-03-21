package com.niu.core.listener.member;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.event.pay.TransferSuccessEvent;
import com.niu.core.service.core.member.ICoreMemberCashOutService;
import com.niu.core.service.core.site.ICoreSiteAccountService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 会员提现转账成功
 */
@EventListen("core")
@Component
public class MemberCashOutTransferSuccessListener extends AbstractListener {

    @Resource
    ICoreMemberCashOutService coreMemberCashOutService;

    public void handleEvent(TransferSuccessEvent event) {
        PayTransfer transfer = event.getTransfer();
        if (transfer.getTradeType().equals("member_cash_out")) {
            coreMemberCashOutService.transferFinish(transfer.getTransferNo());
        }
    }
}
