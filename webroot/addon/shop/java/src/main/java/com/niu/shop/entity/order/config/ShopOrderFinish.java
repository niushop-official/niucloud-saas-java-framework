package com.niu.shop.entity.order.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单完成配置
 */
@Data
public class ShopOrderFinish implements Serializable {

    private static final long serialVersionUID = 1L;
    private Boolean isFinish; // 是否自动完成
    private Integer finishLength; // 完成时长

}