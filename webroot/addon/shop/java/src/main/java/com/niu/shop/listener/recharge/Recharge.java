package com.niu.shop.listener.recharge;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员充值套餐实体
 */
@Data
public class Recharge implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Integer rechargeId;

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 套餐名称
     */
    private String rechargeName;

    /**
     * 面值
     */
    private BigDecimal faceValue;

    /**
     * 价格
     */
    private BigDecimal buyPrice;

    /**
     * 赠送积分
     */
    private Integer point;

    /**
     * 赠送成长值
     */
    private Integer growth;

    /**
     * 发放数量
     */
    private Integer saleNum;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 状态（0关闭 1开启）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

    private String giftJson;

}