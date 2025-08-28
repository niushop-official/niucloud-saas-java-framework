package com.niu.shop.event.conpon;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;

@Data
public class SendCouponEvent extends Event {

    private Integer recordId;


}
