package com.niu.shop.listener.refund;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.order.OrderLogMainTypeEnum;
import com.niu.shop.enums.order.OrderRefundActionTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.event.refund.AfterShopOrderRefundApplyEvent;
import com.niu.shop.event.refund.AfterShopOrderRefundCloseEvent;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.order.ICoreOrderDeliveryService;
import com.niu.shop.service.core.order.param.OrderDeliveryParam;
import com.niu.shop.service.core.refund.ICoreRefundService;
import com.niu.shop.service.core.refund.param.AddRefundLogParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@EventListen("shop")
@Component
public class AfterShopOrderRefundApplyListener extends AbstractListener {

    @Resource
    ICoreRefundService coreRefundService;

    @Resource
    private ShopOrderMapper shopOrderMapper;

    @Resource
    private ICoreOrderDeliveryService coreOrderDeliveryService;


    public void handleEvent(AfterShopOrderRefundApplyEvent event) {
        ShopOrderRefund orderRefund = event.getOrderRefund();

        ShopOrder order = shopOrderMapper.selectById(orderRefund.getOrderId());
        //通知三方应用更新订单状态
        if (StringUtils.isNotEmpty(order.getRelateSource())) {
            CommonEventDefiner.CommonEvent sendEvent = new CommonEventDefiner.CommonEvent();
            Map<String, Object> data = new HashMap<>();
            data.put("source_order_id", orderRefund.getOrderId());
            data.put("relate_source", order.getRelateSource());
            data.put("relate_order_id", order.getRelateOrderId());
            data.put("refund_type", orderRefund.getRefundType());
            data.put("refund_reason", orderRefund.getReason());
            data.put("remark", orderRefund.getRemark());
            data.put("refund_voucher", orderRefund.getVoucher());
            data.put("order_refund_no", orderRefund.getOrderRefundNo());
            sendEvent.setSource("shop");
            sendEvent.setEventName("ThirdOrderRefundApplyNotice");
            sendEvent.setData(data);
            sendEvent.setSiteId(orderRefund.getSiteId());
            EventAndSubscribeOfPublisher.publishAll(sendEvent);
        }

        String mainType = OrderLogMainTypeEnum.MEMBER.getType();
        Integer mainId = orderRefund.getMemberId();

        // 添加退款日志
        AddRefundLogParam addLog = new AddRefundLogParam().builder()
                .orderRefundNo(orderRefund.getOrderRefundNo())
                .status(Integer.parseInt(orderRefund.getStatus().toString()))
                .mainType(mainType)
                .mainId(mainId)
                .type(OrderRefundActionTypeEnum.APPLY_ACTION.getType())
                .content("").build();
        coreRefundService.addLog(addLog);
    }
}
