package com.niu.core.service.core.sys.event;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysPrinterPrintTicketEvent extends Event {
    private SysPrinterPrintTicketParam param;
}
