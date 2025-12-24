package com.niu.core.event.member;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.member.MemberCashOut;
import lombok.Data;

@Data
public class AfterCashFinishEvent extends Event {

    private MemberCashOut cashOut;
}
