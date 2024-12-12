package com.niu.shop.service.core.delivery.electronic.sheet.engine;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class ApplyParam {
    @JsonProperty("ShipperCode")
    private String shipperCode;
    @JsonProperty("CustomerName")
    private String customerName;
    @JsonProperty("CustomerPwd")
    private String customerPwd;
    @JsonProperty("SendSite")
    private String sendSite;
    @JsonProperty("SendStaff")
    private String sendStaff;
    @JsonProperty("MonthCode")
    private String monthCode;
    @JsonProperty("OrderCode")
    private String orderCode;
    @JsonProperty("PayType")
    private Integer payType;
    @JsonProperty("ExpType")
    private String expType;
    @JsonProperty("Sender")
    private Sender sender;
    @JsonProperty("Receiver")
    private Receiver receiver;
    @JsonProperty("Quantity")
    private Integer quantity;
    @JsonProperty("Commodity")
    private List<Commodity> commodity;
    @JsonProperty("Weight")
    private BigDecimal weight;
    @JsonProperty("Volume")
    private BigDecimal volume;
    @JsonProperty("Cost")
    private BigDecimal cost;
    @JsonProperty("Remark")
    private String remark;
    @JsonProperty("IsReturnPrintTemplate")
    private String isReturnPrintTemplate;
    @JsonProperty("TemplateSize")
    private String templateSize;
    @JsonProperty("IsNotice")
    private Integer isNotice;

    @Data
    @Accessors(chain = true)
    public static class Sender {
        @JsonProperty("Name")
        private String name;
        @JsonProperty("Mobile")
        private String mobile;
        @JsonProperty("ProvinceName")
        private String provinceName;
        @JsonProperty("CityName")
        private String cityName;
        @JsonProperty("ExpAreaName")
        private String expAreaName;
        @JsonProperty("Address")
        private String address;
        @JsonProperty("PostCode")
        private String postCode;
    }

    @Data
    @Accessors(chain = true)
    public static class Receiver {
        @JsonProperty("Name")
        private String name;
        @JsonProperty("Mobile")
        private String mobile;
        @JsonProperty("ProvinceName")
        private String provinceName;
        @JsonProperty("CityName")
        private String cityName;
        @JsonProperty("ExpAreaName")
        private String expAreaName;
        @JsonProperty("Address")
        private String address;
        @JsonProperty("PostCode")
        private String postCode;
    }

    @Data
    public static class Commodity {
        @JsonProperty("GoodsName")
        private String goodsName;
        @JsonProperty("GoodsCode")
        private String goodsCode;
        @JsonProperty("Goodsquantity")
        private Integer goodsquantity;
        @JsonProperty("GoodsPrice")
        private Double goodsPrice;
        @JsonProperty("GoodsWeight")
        private Double goodsWeight;
        @JsonProperty("GoodsDesc")
        private String goodsDesc;
        @JsonProperty("GoodsVol")
        private Double goodsVol;
    }

}
