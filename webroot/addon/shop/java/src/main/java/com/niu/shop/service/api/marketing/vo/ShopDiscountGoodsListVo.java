package com.niu.shop.service.api.marketing.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.active.ActiveTypeEnum;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.service.api.goods.vo.ShopGoodsListVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * ShopActive列表视图
 */
@Data
public class ShopDiscountGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsId;  // 商品id
    private Integer siteId;  // 站点id
    private String goodsName;  // 商品名称
    private String goodsType;  // 商品类型
    private String goodsCover;  // 商品封面
    private String goodsCoverThumbSmall;
    private String goodsCoverThumbMid;
    private Integer saleNum;  // 销量
    private Integer status;  // 商品状态（1.正常0下架）
    private Integer stock;
    private Integer sort;  // 排序
    private String unit;
    private String goodsTypeName;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    private String memberDiscount;
    @JsonProperty(value = "goodsSku")
    private GoodsSku goodsSku;
    private String activeGoodsValue;
    @JsonProperty(value = "activeGoods")
    private ActiveGoods activeGoods;
    private String activeGoodsStatus;

    @Data
    public static class ActiveGoods {
        private String activeGoodsStatus;
        private String activeGoodsStatusName;

        public String getActiveGoodsStatusName() {
            return ActiveStatusEnum.getNameByStatus(this.activeGoodsStatus);
        }
    }

    @Data
    public static class GoodsSku {
        private Integer skuId; // 商品sku_id
        private Integer siteId; // 站点id
        private String skuName; // 商品sku名称
        private String skuImage; // sku主图
        private String skuNo; // 商品sku编码
        private Integer goodsId; // 商品id
        private String skuSpecFormat; // sku规格格式
        private BigDecimal price; // sku单价
        private BigDecimal marketPrice; // 划线价
        private BigDecimal salePrice; // 实际卖价（有活动显示活动价，默认原价）
        private BigDecimal costPrice; // sku成本价
        private Integer stock; // 商品sku库存
        private BigDecimal weight; // 重量（单位kg）
        private BigDecimal volume; // 体积（单位立方米）
        private Integer saleNum; // 销量
        private Integer isDefault; // 是否默认
        private BigDecimal activeDiscountPrice;
        private BigDecimal activeDiscountRate;
        private String activeDiscountType;
        private BigDecimal activeReduceMoney;
        private BigDecimal activeSpecifyPrice;
    }

    public String getGoodsTypeName() {
        return GoodsTypeEnum.getNameByType(this.goodsType);
    }


    public String getGoodsCoverThumbSmall() {
        return CommonUtils.thumbImageSmall(this.siteId, this.getGoodsCover());
    }

    public String getGoodsCoverThumbMid() {
        return CommonUtils.thumbImageMiddle(this.siteId, this.getGoodsCover());
    }


}
