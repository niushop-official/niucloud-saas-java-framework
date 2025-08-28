package com.niu.shop.listener.refund;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.order.OrderLogMainTypeEnum;
import com.niu.shop.enums.order.OrderRefundActionTypeEnum;
import com.niu.shop.enums.order.OrderRefundStatusEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.event.order.ShopOrderRefundActiveCreateAfterEvent;
import com.niu.shop.event.refund.AfterShopOrderRefundCloseEvent;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.order.ICoreOrderDeliveryService;
import com.niu.shop.service.core.order.param.OrderDeliveryParam;
import com.niu.shop.service.core.refund.ICoreRefundService;
import com.niu.shop.service.core.refund.param.ActiveRefundParam;
import com.niu.shop.service.core.refund.param.AddRefundLogParam;
import com.niu.shop.service.core.refund.param.RefundFinishParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@EventListen("shop")
@Component
public class AfterShopOrderRefundCloseListener extends AbstractListener {

    @Resource
    ICoreRefundService coreRefundService;

    @Resource
    private ShopOrderMapper shopOrderMapper;

    @Resource
    private ICoreOrderDeliveryService coreOrderDeliveryService;


    public void handleEvent(AfterShopOrderRefundCloseEvent event) {
        Map<String, Object> refundData = event.getRefundData();

        // 查询订单信息
        ShopOrder order = shopOrderMapper.selectOne(new LambdaQueryWrapper<ShopOrder>()
                .eq(ShopOrder::getOrderId, refundData.get("order_id")));

        if (order != null && OrderStatusEnum.WAIT_DELIVERY.equals(order.getStatus())) {
            // 校验订单项是否全部发货
            OrderDeliveryParam orderDeliveryParam = new OrderDeliveryParam();
            orderDeliveryParam.setOrderId(Integer.parseInt(refundData.get("order_id").toString()));
            coreOrderDeliveryService.checkFinish(orderDeliveryParam);
        }

        // 设置日志参数
        String mainType = event.getMainType() != null ? event.getMainType() : OrderLogMainTypeEnum.MEMBER.getType();
        Integer mainId = event.getMainId() != null ? event.getMainId() : (Integer) refundData.get("member_id");

        // 添加退款日志
        AddRefundLogParam addLog = new AddRefundLogParam().builder()
                .orderRefundNo(refundData.get("order_refund_no").toString())
                .status(Integer.parseInt(refundData.get("status").toString()))
                .mainType(mainType)
                .mainId(mainId)
                .type(OrderRefundActionTypeEnum.CLOSE_ACTION.getType())
                .content("").build();
        coreRefundService.addLog(addLog);
    }
}
