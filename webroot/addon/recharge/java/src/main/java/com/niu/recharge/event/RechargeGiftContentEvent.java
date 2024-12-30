package com.niu.recharge.event;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RechargeGiftContentEvent extends Event {
    private String key;
    private Object value;
}
