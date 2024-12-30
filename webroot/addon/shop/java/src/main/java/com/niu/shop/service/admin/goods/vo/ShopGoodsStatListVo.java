package com.niu.shop.service.admin.goods.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品数据统计列表
 */
@Data
public class ShopGoodsStatListVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Integer id;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 日期
     */
    private String date;

    /**
     * 时间戳
     */
    private Long dateTime;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 加入购物车数量
     */
    private Integer cartNum;

    /**
     * 商品销量（下单数）
     */
    private Integer saleNum;

    /**
     * 支付件数
     */
    private Integer payNum;

    /**
     * 支付总金额
     */
    private BigDecimal payMoney;

    /**
     * 退款件数
     */
    private Integer refundNum;

    /**
     * 退款总额
     */
    private BigDecimal refundMoney;

    /**
     * 访问次数（浏览量）
     */
    private Integer accessNum;

    /**
     * 收藏数量
     */
    private Integer collectNum;

    /**
     * 评论数量
     */
    private Integer evaluateNum;

    /**
     * 商品访客数
     */
    private Integer goodsVisitMemberCount;


}
