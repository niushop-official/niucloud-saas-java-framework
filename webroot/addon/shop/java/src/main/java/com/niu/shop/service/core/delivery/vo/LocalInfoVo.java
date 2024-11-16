package com.niu.shop.service.core.delivery.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class LocalInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String feeType; // 费用类型
    private BigDecimal baseDist; // 多少km内
    private BigDecimal basePrice; // 配送费用
    private BigDecimal gradDist; // 每超出多少km内
    private BigDecimal gradPrice; // 配送费用
    private BigDecimal weightStart; // 重量多少内不额外收费
    private BigDecimal weightUnit; // 每超出多少kg
    private BigDecimal weightPrice; // 价格
    private String deliveryType; // 配送类型
    private List<Area> areas; // 配送区域
    private LatLnt centers; // 发货地址中心点

    @Data
    public static class Area {
        private String areaName;
        private String areaType;
        private BigDecimal startPrice;
        private BigDecimal deliveryPrice;
        private AreaJson areaJson;
    }

    @Data
    public static class LatLnt {
        private BigDecimal lat;
        private BigDecimal lng;
    }

    @Data
    public static class AreaJson {
        private String key;
        private Integer radius;
        private LatLnt center;
        private List<LatLnt> paths;
    }

}
