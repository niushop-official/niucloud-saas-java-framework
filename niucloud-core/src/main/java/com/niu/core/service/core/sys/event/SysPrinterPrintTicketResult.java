package com.niu.core.service.core.sys.event;

import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.entity.sys.SysPrinter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysPrinterPrintTicketResult extends EventResult {
    private int code;
    private String message;
    private List<Item> data;

    @Data
    public static class Item {
        private SysPrinter printerInfo;
        private String originId;
        private String content;
    }
}
