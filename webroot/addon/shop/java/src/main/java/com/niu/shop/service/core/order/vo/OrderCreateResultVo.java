package com.niu.shop.service.core.order.vo;

import com.niu.shop.entity.order.config.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单配置数据
 */
@Data
public class OrderCreateResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tradeType;

    private Integer orderId;

}
