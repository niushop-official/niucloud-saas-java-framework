package com.niu.shop.service.core.shop.vo;

import lombok.Data;
import org.checkerframework.checker.nullness.qual.EnsuresKeyForIf;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Data
public class StatDataVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> orderNum = new LinkedList<>(); // 订单总数

    private List<BigDecimal> saleMoney = new LinkedList<>(); // 销售总额

    private List<BigDecimal> refundMoney = new LinkedList<>(); // 退款总额

    private List<Integer> accessSum = new LinkedList<>(); // 访问数

    private List<String> time = new LinkedList<>();
}
