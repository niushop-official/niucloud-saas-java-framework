package com.niu.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 【请填写功能名称】实体
 */
@Data
public class ShopStat implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id; // 
    private Integer siteId; // 站点id
    private String date; // 日期
    private Long dateTime; // 时间戳
    private Integer orderNum; // 订单总数
    private BigDecimal saleMoney; // 销售总额
    private BigDecimal refundMoney; // 退款总额
    private Integer accessSum; // 访问数

}