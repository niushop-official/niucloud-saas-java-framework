package com.niu.shop.service.admin.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.shop.entity.goods.ShopGoodsSku;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetBuyGoodsSelectVo {
    private Integer goodsId; // 商品id
    private Integer siteId;
    private String goodsName;
    private String goodsType;
    private String subTitle;
    private String goodsCover;
    private String goodsImage;
    private String goodsVideo;
    private String goodsCategory;
    private String goodsDesc;
    private Integer brandId;
    private String labelIds;
    private String serviceIds;
    private String unit;
    private Integer stock;
    private Integer saleNum;
    private Integer virtualSaleNum;
    private Integer status;
    private Integer sort;
    private String deliveryType;
    private String feeType;
    private BigDecimal deliveryMoney;
    private Integer deliveryTemplateId;
    private Integer virtualAutoDelivery;
    private String virtualReceiveType;
    private Integer virtualVerifyType;
    private Integer virtualIndate;
    private Integer supplierId;
    private Integer attrId;
    private String attrFormat;
    private Integer isDiscount;
    private String memberDiscount;
    private Integer posterId;
    private Integer isLimit;
    private Integer limitType;
    private Integer maxBuy;
    private Integer minBuy;
    private Integer isGift;
    private Integer accessNum;
    private Integer cartNum;
    private Integer payNum;
    private BigDecimal payMoney;
    private Integer collectNum;
    private Integer evaluateNum;
    private Integer refundNum;
    private BigDecimal refundMoney;
    private String createTime;
    private String goodsTypeName;
    private String goodsCoverThumbSmall;
    private String goodsCoverThumbMid;
    private Integer hasBuy;
    private ShopManjianRuleContentVo manjianInfo;
    @JsonProperty("goodsSku")
    private ShopGoodsSku goodsSku;
}
