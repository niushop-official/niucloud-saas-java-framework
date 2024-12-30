package com.niu.recharge.event;

import com.niu.core.common.component.context.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Accessors(chain = true)
@NoArgsConstructor
public class RechargeAfterEvent extends Event {
    private Integer memberId;
    private String giftJson;
}
