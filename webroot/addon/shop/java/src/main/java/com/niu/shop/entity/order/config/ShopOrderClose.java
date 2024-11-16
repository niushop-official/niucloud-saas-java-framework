package com.niu.shop.entity.order.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单关闭配置
 */
@Data
public class ShopOrderClose implements Serializable {

    private static final long serialVersionUID = 1L;
    private Boolean isClose; // 是否关闭
    private Integer closeLength; // 关闭时长

}