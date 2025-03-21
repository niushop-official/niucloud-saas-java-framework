package com.niu.shop.listener.refund;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.order.OrderRefundActionTypeEnum;
import com.niu.shop.enums.order.OrderRefundStatusEnum;
import com.niu.shop.event.order.ShopOrderRefundActiveCreateAfterEvent;
import com.niu.shop.service.core.refund.ICoreRefundService;
import com.niu.shop.service.core.refund.param.ActiveRefundParam;
import com.niu.shop.service.core.refund.param.AddRefundLogParam;
import com.niu.shop.service.core.refund.param.RefundFinishParam;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.math.BigDecimal;

@EventListen("shop")
@Component
public class ShopOrderRefundActiveCreateAfterListener extends AbstractListener {

    @Resource
    ICoreRefundService coreRefundService;

    public void handleEvent(ShopOrderRefundActiveCreateAfterEvent event) {
        ActiveRefundParam refundParam = event.getRefundParam();
        ShopOrderRefund refund = event.getRefund();
        ShopOrderGoods orderGoods = event.getOrderGoods();

        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setStatus(Integer.valueOf(refund.getStatus()));
        logParam.setMainType(refundParam.getMainType());
        logParam.setMainId(refundParam.getMainId());
        logParam.setType(OrderRefundActionTypeEnum.ACTIVE_REFUND_ACTION.getType());
        logParam.setContent("商品【" + orderGoods.getGoodsName() + " " + orderGoods.getSkuName() + "】商家主动退款，退款金额：" + refund.getMoney() + "元，退款说明：" + refund.getRemark());
        coreRefundService.addLog(logParam);

        if (refund.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString())) {
            if (refund.getMoney().compareTo(BigDecimal.ZERO) > 0) {
                coreRefundService.toTransfer(refund);
            } else {
                RefundFinishParam finishParam = new RefundFinishParam();
                finishParam.setOrderRefundNo(refund.getOrderRefundNo());
                coreRefundService.finish(finishParam);
            }
        }
    }
}
