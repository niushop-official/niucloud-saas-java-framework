package com.niu.core.event.pay;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.pay.Pay;
import lombok.Data;

@Data
public class PaySuccessEvent extends Event {

    private String tradeType;

    private Integer tradeId;

    private Pay pay;

}
