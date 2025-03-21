package com.niu.shop.listener.recharge;

import com.niu.core.common.component.context.event.EventResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RechargeGiftContentVo extends EventResult {
    private List<String> detail;
    private String info;
    private String label;
}
