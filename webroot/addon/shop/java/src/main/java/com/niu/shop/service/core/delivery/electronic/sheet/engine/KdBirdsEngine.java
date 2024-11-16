package com.niu.shop.service.core.delivery.electronic.sheet.engine;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
class KdBirdsEngine implements ElectronicSheetEngine {
    private static final String ELECTRONIC_SHEET_URL = "https://api.kdniao.com/api/Eorderservice";

    private final String kdBirdId;
    private final String kdBirdApiKey;

    public KdBirdsEngine(String kdBirdId, String kdBirdApiKey) {
        this.kdBirdId = kdBirdId;
        this.kdBirdApiKey = kdBirdApiKey;
    }

    @Override
    public ApplyResult apply(String cacheKey, ApplyParam param) {
        String requestData = JSON.toJSONString(param);
        ElectronicSheetReq req = new ElectronicSheetReq();
        req.setEBusinessId(kdBirdId);
        req.setRequestType("1007");
        req.setRequestData(requestData);
        req.setDataType("2");
        req.setDataSign(sign(requestData));

        Cached cacheOperator = CacheFactory.getCacheOperator();
        Object cache = cacheOperator.get(cacheKey);
        if (ObjectUtil.isNotEmpty(cache)) {
            return (ApplyResult) cache;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        JSON.parseObject(JSON.toJSONString(req)).forEach(multiValueMap::add);

        ResponseEntity<String> response = new RestTemplate()
                .postForEntity(ELECTRONIC_SHEET_URL, new HttpEntity<>(multiValueMap, headers), String.class);

        String body = response.getBody();
        if (body == null) {
            throw new RuntimeException("电子面单申请失败，请重试");
        }

        ElectronicSheetResp result = JSON.parseObject(body, ElectronicSheetResp.class);
        ApplyResult applyResult = new ApplyResult();
        applyResult.setSuccess(result.getSuccess());
        applyResult.setResultCode(result.getResultCode());
        applyResult.setReason(result.getReason());
        if (result.getSuccess()) {
            applyResult.setPrintTemplate(result.getPrintTemplate());
            applyResult.setOrderNo(result.getOrder().getOrderCode());
            applyResult.setOrderInfo(result.getOrder());
        }

        cacheOperator.tag("electronic_sheet_cache_kdbird").put(cacheKey, applyResult);
        return applyResult;
    }

    private String sign(String data) {
        String md5 = DigestUtils.md5DigestAsHex((data + kdBirdApiKey).getBytes());
        byte[] base64 = Base64.getEncoder().encode(md5.getBytes());
        return urlEncode(new String(base64));
    }

    private String urlEncode(String raw) {
        try {
            return URLEncoder.encode(raw, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("面单申请失败，请重试");
        }
    }

    @Data
    private static class ElectronicSheetReq {
        @JsonProperty("EBusinessID")
        private String eBusinessId;
        @JsonProperty("RequestType")
        private String requestType;
        @JsonProperty("RequestData")
        private String requestData;
        @JsonProperty("DataType")
        private String dataType;
        @JsonProperty("DataSign")
        private String dataSign;
    }

    @NoArgsConstructor
    @Data
    private static class ElectronicSheetResp {
        @JsonProperty("EBusinessID")
        private String eBusinessID;
        @JsonProperty("Success")
        private Boolean success;
        @JsonProperty("ResultCode")
        private Integer resultCode;
        @JsonProperty("Reason")
        private String reason;
        @JsonProperty("Order")
        private Order order;
        @JsonProperty("PrintTemplate")
        private String printTemplate;
        @JsonProperty("UniquerRequestNumber")
        private String uniquerRequestNumber;
        @JsonProperty("SubCount")
        private Integer subCount;
        @JsonProperty("SubOrders")
        private String subOrders;
        @JsonProperty("SubPrintTemplates")
        private String subPrintTemplates;
        @JsonProperty("SignBillPrintTemplate")
        private String signBillPrintTemplate;

        @NoArgsConstructor
        @Data
        public static class Order {
            @JsonProperty("OrderCode")
            private String orderCode;
            @JsonProperty("ShipperCode")
            private String shipperCode;
            @JsonProperty("LogisticCode")
            private String logisticCode;
            @JsonProperty("MarkDestination")
            private String markDestination;
            @JsonProperty("SignWaybillCode")
            private String signWaybillCode;
            @JsonProperty("OriginCode")
            private String originCode;
            @JsonProperty("OriginName")
            private String originName;
            @JsonProperty("DestinatioCode")
            private String destinatioCode;
            @JsonProperty("DestinatioName")
            private String destinatioName;
            @JsonProperty("SortingCode")
            private String sortingCode;
            @JsonProperty("PackageCode")
            private String packageCode;
            @JsonProperty("PackageName")
            private String packageName;
            @JsonProperty("DestinationAllocationCentre")
            private String destinationAllocationCentre;
            @JsonProperty("TransportType")
            private Integer transportType;
            @JsonProperty("ShipperInfo")
            private String shipperInfo;
        }
    }
}
