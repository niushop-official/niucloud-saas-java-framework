package com.niu.core.event.member;

import com.niu.core.component.context.event.Event;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberAccountEvent extends Event {

    private String accountType;

    private Integer memberId;

    private BigDecimal accountData;

    private BigDecimal accountNewData;
}
