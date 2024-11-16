package com.niu.core.event.pay;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;

@Data
public class PayCloseEvent extends Event {

    private String tradeType;

    private Integer tradeId;

}
