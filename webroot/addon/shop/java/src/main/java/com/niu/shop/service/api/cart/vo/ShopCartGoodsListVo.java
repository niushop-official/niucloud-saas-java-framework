package com.niu.shop.service.api.cart.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.service.api.marketing.vo.ShopManjianInfoVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ShopCart列表视图
 */
@Data
public class ShopCartGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;  // 购物车表ID
    private Integer siteId;  // 站点id
    private Integer memberId;  // 会员ID
    private Integer goodsId;  // 商品ID
    private Integer skuId;  // sku id
    private Integer num;  // 商品数量
    private Integer marketType;  // 活动类型
    private Integer marketTypeId;  // 活动id
    private Integer status;  // 购物车商品状态
    private String invalidRemark;  // 失效原因
    private Goods goods;
    @JsonProperty("goodsSku")
    private GoodsSku goodsSku;
    private List<GoodsSpec> goodsSpec;
    private List<ShopManjianInfoVo> manjianInfo;

    private BigDecimal showPrice;

    @Data
    public static class Goods {
        private Integer goodsId; // 商品id
        private String goodsName; // 商品名称
        private String goodsType; // 商品类型
        private String subTitle; // 副标题
        private Integer siteId;
        private String goodsCover; // 商品封面
        private String goodsCoverThumbBig;
        private String goodsCoverThumbSmall;
        private String goodsCoverThumbMid;
        private Integer status;
        private String unit; // 单位
        private Integer stock; // 商品库存（总和）
        private Integer saleNum; // 销量
        private Integer isDiscount; // 是否参与限时折扣
        private String memberDiscount;
        private Long deleteTime; // 创建时间
        private Integer hasBuy; //已购买数量


        public String getGoodsCoverThumbSmall() {
            return CommonUtils.thumbImageSmall(this.siteId, this.getGoodsCover());
        }

        public String getGoodsCoverThumbMid() {
            return CommonUtils.thumbImageMiddle(this.siteId, this.getGoodsCover());
        }

        public String getGoodsCoverThumbBig() {
            return CommonUtils.thumbImageBig(this.siteId, this.getGoodsCover());
        }
    }

    @Data
    public static class GoodsSku {
        private Integer skuId; // 商品sku_id
        private Integer siteId; // 站点id
        private String skuName; // 商品sku名称
        private String skuImage; // sku主图
        private String skuImageThumbBig;
        private String skuImageThumbSmall;
        private String skuImageThumbMid;
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
        private BigDecimal memberPrice;
        private BigDecimal showPrice;
        private String showType;

        public String getSkuImageThumbSmall() {
            return CommonUtils.thumbImageSmall(this.siteId, this.getSkuImage());
        }

        public String getSkuImageThumbMid() {
            return CommonUtils.thumbImageMiddle(this.siteId, this.getSkuImage());
        }

        public String getSkuImageThumbBig() {
            return CommonUtils.thumbImageBig(this.siteId, this.getSkuImage());
        }
    }

    @Data
    public static class GoodsSpec {
        private Integer specId;  // 规格id
        private Integer goodsId;  // 关联商品id
        private String specName;  // 规格项名称
        private String specValues;  // 规格值名称，多个逗号隔开
    }
}
