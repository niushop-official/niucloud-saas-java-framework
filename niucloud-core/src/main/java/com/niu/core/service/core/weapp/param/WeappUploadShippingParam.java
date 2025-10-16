package com.niu.core.service.core.weapp.param;

import lombok.Data;
import java.util.List;

@Data
public class WeappUploadShippingParam {
    private String outTradeNo;
    private Integer logisticsType;
    private Integer deliveryMode;
    private List<UploadShippingParam> shippingList;
    private String weappOpenid;
    private Boolean isAllDelivered;

    @Data
    public static class UploadShippingParam {
        private String trackingNo;
        private String expressCompany;
        private String itemDesc;
        private ContactInfo contact;

    }
    @Data
    public static class ContactInfo {
        private String consignorContact;
        private String receiverContact;
    }
}

