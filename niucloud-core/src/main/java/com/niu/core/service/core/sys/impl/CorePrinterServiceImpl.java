package com.niu.core.service.core.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.event.CallbackPublisher;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.sys.SysPrinter;
import com.niu.core.enums.sys.SysPrinterBrandEnum;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICorePrinterService;
import com.niu.core.service.core.sys.event.SysPrinterPrintTicketEvent;
import com.niu.core.service.core.sys.event.SysPrinterPrintTicketResult;
import com.niu.core.service.core.sys.param.SysPrinterAddPrinterYlyParam;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import com.niu.core.service.core.sys.sdk.yly.YlyPrinterSdk;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.sys.vo.SysPrinterPrintTicketVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorePrinterServiceImpl implements ICorePrinterService {
    private final ICoreConfigService coreConfigService;

    @Override
    public void setYlyTokenConfig(JSON config) {
        coreConfigService.setConfig(RequestUtils.siteId(), "PRINTER_YLY_TOKEN", config);
    }

    @Override
    public JSONObject getYlyTokenConfig() {
        CoreSysConfigVo config = coreConfigService.getConfig(RequestUtils.siteId(), "PRINTER_YLY_TOKEN");
        if (ObjectUtil.isEmpty(config)) {
            JSONObject valueJsonObject = new JSONObject();
            valueJsonObject.set("access_token", "")
                    .set("end_time", "0");
            return valueJsonObject;
        }

        return JSONUtil.parseObj(config.getValueJson());
    }

    @Override
    public String getYlyToken(String id, String secret) {
        return getYlyToken(id, secret, false);
    }

    @Override
    public void addPrinterYly(SysPrinterAddPrinterYlyParam param) {
        getSdk(param.getOpenId(), param.getApikey())
                .addPrinter(param.getPrinterCode(), param.getPrinterKey());
    }

    @Override
    public void deletePrinterYly(SysPrinter printer) {
        getSdk(printer.getOpenId(), printer.getApikey())
                .printerDeletePrinter(printer.getPrinterCode());
    }

    @Override
    public SysPrinterPrintTicketVo printTicket(SysPrinterPrintTicketParam param) {
        SysPrinterPrintTicketEvent event = new SysPrinterPrintTicketEvent();
        event.setSiteId(param.getSiteId());
        event.setParam(param);
        event.setAuthority(false);
        List<SysPrinterPrintTicketResult> results = CallbackPublisher.publishReturnList(event)
                .stream()
                .map(result -> (SysPrinterPrintTicketResult) result)
                .collect(Collectors.toList());

        SysPrinterPrintTicketVo vo = new SysPrinterPrintTicketVo();
        Optional<SysPrinterPrintTicketResult> error = results.stream().filter(result -> result.getCode() != 0).findFirst();
        if (error.isPresent()) {
            SysPrinterPrintTicketResult result = error.get();
            vo.setCode(result.getCode());
            vo.setMessage(result.getMessage());
            return vo;
        }

        List<SysPrinterPrintTicketResult.Item> items = results.stream()
                .flatMap(result -> result.getData().stream())
                .collect(Collectors.toList());
        if (ObjectUtil.isEmpty(items)) {
            vo.setCode(-1);
            vo.setMessage("未找到小票模板内容");
            return vo;
        }

        try {
            for (SysPrinterPrintTicketResult.Item item : items) {
                if (ObjectUtil.isEmpty(item.getPrinterInfo())) {
                    continue;
                }

                SysPrinter printer = item.getPrinterInfo();
                if (printer == null) {
                    continue;
                }

                if (Objects.requireNonNull(SysPrinterBrandEnum.getEnumByBrand(printer.getBrand())) == SysPrinterBrandEnum.YI_LIAN_YUN) {
                    YlyPrinterSdk sdk = getSdk(printer.getOpenId(), printer.getApikey());
                    sdk.printIndex(printer.getPrinterCode(), item.getContent(), String.valueOf(item.getOriginId()));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            vo.setCode(-1);
            vo.setMessage(e.getMessage());
        }

        return vo;
    }

    @Override
    public String refreshToken(SysPrinter printer) {
        return getYlyToken(printer.getOpenId(), printer.getPrinterKey(), true);
    }

    @Override
    public void printIndex(SysPrinter printer, String content, String originId) {
        YlyPrinterSdk sdk = getSdk(printer.getOpenId(), printer.getApikey());
        sdk.printIndex(printer.getPrinterCode(), content, originId);
    }

    private String getYlyToken(String id, String secret, boolean refresh) {
        YlyPrinterSdk sdk = getSdk(id, secret, refresh);
        JSONObject tokenData = new JSONObject()
                .set("access_token", sdk.getToken())
                .set("end_time", -1);
        setYlyTokenConfig(tokenData);
        return sdk.getToken();
    }

    private YlyPrinterSdk getSdk(String id, String secret) {
        return getSdk(id, secret, false);
    }

    private YlyPrinterSdk getSdk(String id, String secret, boolean refresh) {
        YlyPrinterSdk sdk;
        String accessToken = getYlyTokenConfig().getStr("access_token");
        if (refresh || ObjectUtil.isEmpty(accessToken)) {
            sdk = new YlyPrinterSdk(id, secret);
            sdk.getAccessToken();
        } else {
            sdk = new YlyPrinterSdk(id, secret, accessToken);
        }
        if (ObjectUtil.isEmpty(accessToken)) {
            JSONObject valueJsonObject = new JSONObject();
            valueJsonObject.set("access_token", sdk.getToken())
                    .set("end_time", "0");
            setYlyTokenConfig(valueJsonObject);
        }
        return sdk;
    }
}
