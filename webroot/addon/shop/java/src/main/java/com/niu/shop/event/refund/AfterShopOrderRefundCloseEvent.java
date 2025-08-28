package com.niu.shop.event.refund;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;

import java.util.Map;

@Data
public class AfterShopOrderRefundCloseEvent extends Event {

    private String orderRefundNo;

    private Map<String, Object> refundData;

    private String mainType;

    private Integer mainId;
}
