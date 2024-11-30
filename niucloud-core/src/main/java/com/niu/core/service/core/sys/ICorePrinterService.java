package com.niu.core.service.core.sys;

import cn.hutool.json.JSON;
import com.niu.core.entity.sys.SysPrinter;
import com.niu.core.service.core.sys.param.SysPrinterAddPrinterYlyParam;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import com.niu.core.service.core.sys.vo.SysPrinterPrintTicketVo;

public interface ICorePrinterService {
    void setYlyTokenConfig(JSON config);

    JSON getYlyTokenConfig();

    String getYlyToken(String id, String secret);

    void addPrinterYly(SysPrinterAddPrinterYlyParam param);

    void deletePrinterYly(SysPrinter printer);

    SysPrinterPrintTicketVo printTicket(SysPrinterPrintTicketParam param);

    String refreshToken(SysPrinter printer);

    void printIndex(SysPrinter printer, String content, String originId);
}
