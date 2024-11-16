package com.niu.shop.service.core.delivery.param;

import lombok.Data;

import java.util.List;

@Data
public class ShopDeliveryPrintElectronicSheetParam {
    private Integer siteId;
    private String printType;
    private Integer orderId;
    private Integer electronicSheetId;
    private List<Integer> orderGoodsIds;
    private List<Parcel> list;

    @Data
    public static class Parcel {
        private Integer deliveryId;
        private Integer electronicSheetId;
    }
}
