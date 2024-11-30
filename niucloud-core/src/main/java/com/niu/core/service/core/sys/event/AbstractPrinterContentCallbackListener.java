package com.niu.core.service.core.sys.event;

import com.niu.core.common.component.context.listener.CallbackListener;

public abstract class AbstractPrinterContentCallbackListener extends CallbackListener<SysPrinterPrintTicketEvent> {
    public abstract SysPrinterPrintTicketResult handleCallback(SysPrinterPrintTicketEvent event);
}
