package com.niu.shop.service.admin.goods.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class GoodsStatCount {
    private Map<String, Integer> accessCount = new LinkedHashMap<>();
    private Map<String, Integer> goodsVisitMemberCount = new LinkedHashMap<>();
    private Map<String, BigDecimal> payMoney = new LinkedHashMap<>();
    private Map<String, BigDecimal> refundMoney = new LinkedHashMap<>();
    private Map<String, Integer> cartNum = new LinkedHashMap<>();
    private Map<String, Integer> saleNum = new LinkedHashMap<>();
    private Map<String, Integer> payNum = new LinkedHashMap<>();
    private Map<String, Integer> refundNum = new LinkedHashMap<>();
}
