package com.niu.shop.service.api.marketing.vo;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.CommonUtils;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * nc_shop_discount_goods数据列表
 */
@Data
public class ShopDiscountGoodsListNewVo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 商品基本信息
    private String goodsTypeName;  // 商品类型名称
    private Integer skuId;  // SKU ID
    private String goodsCoverThumbMid;  // 商品封面图(中图)
    private List<GoodsLabel> goodsLabelName;  // 商品标签名称
    private Integer siteId;  // 站点ID
    private Integer goodsId;  // 商品ID
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String labelIds;  // 标签ID列表
    private String goodsName;  // 商品名称
    private String subTitle;  // 商品副标题
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String goodsCategory;  // 商品分类列表
    private String goodsType;  // 商品类型
    private String goodsCover;  // 商品封面图
    private String unit;  // 单位
    private Integer status;  // 状态
    private Integer saleNum;  // 销量
    private String memberDiscount;  // 会员折扣
    private Integer isDiscount;  // 是否参与折扣
    private Integer discountId;  // 折扣ID
    private String discountStatus;  // 折扣状态
    private String discountStatusName;  // 折扣状态名称
    private String type;  // 折扣类型
    // 折扣相关字段
    private String discountType;  // 折扣类型
    private String discountRate;  // 折扣率
    private String discountPrice;  // 折扣价
    private String reduceMoney;  // 减少的金额

    // SKU信息
    private List<SkuInfo> skuList;  // SKU列表
    @JsonProperty("goodsSku")
    private SkuInfo goodsSku;  // 默认SKU信息

    // SKU内部类
    @Data
    public static class SkuInfo {
        private Integer skuId;  // SKU ID
        private Integer siteId;  // 站点ID
        private String skuName;  // SKU名称
        private String skuImage;  // SKU图片
        private String skuNo;  // SKU编码
        private Integer goodsId;  // 商品ID
        private String skuSpecFormat;  // SKU规格格式
        private BigDecimal price;  // 原价
        private BigDecimal marketPrice;  // 市场价
        private BigDecimal salePrice;  // 售价
        private BigDecimal costPrice;  // 成本价
        private Integer stock;  // 库存
        private BigDecimal weight;  // 重量
        private BigDecimal volume;  // 体积
        private Integer saleNum;  // 销量
        private Integer isDefault;  // 是否默认
        private String memberPrice;  // 会员价格(JSON格式)

        // 折扣相关字段
        private String discountType;  // 折扣类型
        private String discountRate;  // 折扣率
        private String discountPrice;  // 折扣价
        private String reduceMoney;  // 减少的金额
    }

    @Data
    public static class GoodsLabel {
        private Integer labelId;  // 标签ID
        private String labelName;  // 标签名称
        private String styleType;
        private String colorJson;
        private String icon;
    }

    public String getGoodsTypeName() {
        return GoodsTypeEnum.getNameByType(this.goodsType);
    }

    public String getDiscountStatusName() {return ActiveStatusEnum.getNameByStatus(this.discountStatus);}

    public String getGoodsCoverThumbSmall() {
        return CommonUtils.thumbImageSmall(this.siteId, this.getGoodsCover());
    }

    public String getGoodsCoverThumbMid() {
        return CommonUtils.thumbImageMiddle(this.siteId, this.getGoodsCover());
    }


}
