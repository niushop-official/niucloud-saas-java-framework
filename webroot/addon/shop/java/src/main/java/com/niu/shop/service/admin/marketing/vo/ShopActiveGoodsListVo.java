package com.niu.shop.service.admin.marketing.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * ShopActiveGoods列表视图
 */
@Data
public class ShopActiveGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer activeGoodsId;  // 活动商品id
    private Integer activeId;  // 活动id
    private Integer siteId;  // 站点id
    private Integer goodsId;  // 商品id
    private String activeGoodsType;  // 商品活动类型（单品，独立商品，店铺整体商品）
    private String activeClass;  // 商品活动类别
    private String activeGoodsLabel;  // 活动商品标签（针对活动有标签）
    private String activeGoodsCategory;  // 活动商品分类（针对活动有分类）
    private String activeGoodsValue;  // 活动商品信息数据
    private String activeGoodsStatus;  // 活动状态
    private BigDecimal activeGoodsPoint;  // 活动商品积分（展示，搜索）
    private BigDecimal activeGoodsPrice;  // 活动商品价格（展示，搜索）
    private Integer activeGoodsStock;  // 活动商品库存（针对参与库存）
    private BigDecimal activeGoodsOrderMoney;  // 活动累计金额
    private Integer activeGoodsOrderNum;  // 活动累计订单数
    private Integer activeGoodsMemberNum;  // 活动参与会员数
    private Integer activeGoodsSuccessNum;  // 活动成功参与会员数

}
