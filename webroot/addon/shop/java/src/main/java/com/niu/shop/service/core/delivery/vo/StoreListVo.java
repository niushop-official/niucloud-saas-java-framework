package com.niu.shop.service.core.delivery.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class StoreListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer storeId; //
    private String storeName; // 门店名称
    private String storeDesc; // 简介
    private String storeLogo; // 门店logo
    private String storeMobile; // 手机号
    private String fullAddress; // 完整地址
    private String longitude; // 经度
    private String latitude; // 纬度
    private String tradeTime; // 营业时间
    private double distance;
}
