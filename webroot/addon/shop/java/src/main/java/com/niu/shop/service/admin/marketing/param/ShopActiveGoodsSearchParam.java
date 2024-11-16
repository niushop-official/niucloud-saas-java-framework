package com.niu.shop.service.admin.marketing.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 店铺营销活动参数
 */
@Data
public class ShopActiveGoodsSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer activeId;

    private Integer siteId;

    private Integer goodsId;

    private String activeGoodsType;

    private String activeClass;

    private String activeGoodsLabel;

    private String activeGoodsCategory;

    private String activeGoodsValue;

    private String activeGoodsStatus;

    private BigDecimal activeGoodsPoint;

    private BigDecimal activeGoodsPrice;

    private Integer activeGoodsStock;

    private BigDecimal activeGoodsOrderMoney;

    private Integer activeGoodsOrderNum;

    private Integer activeGoodsMemberNum;

    private Integer activeGoodsSuccessNum;

}
