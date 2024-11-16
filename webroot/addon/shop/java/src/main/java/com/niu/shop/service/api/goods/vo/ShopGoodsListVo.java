package com.niu.shop.service.api.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * ShopGoods列表视图
 */
@Data
public class ShopGoodsListVo implements Serializable {

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
    private Integer virtualVerifyType;
    private Integer isDiscount;
    private GoodsSku goodsSku;

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
        private Integer isDefault;
        private BigDecimal memberPrice;
    }

    public String getGoodsTypeName() {
        return GoodsTypeEnum.getNameByType(this.goodsType);
    }

    private ICoreUploadService coreUploadService() {
        return SpringContext.bean(ICoreUploadService.class);
    }

    public String getGoodsCoverThumbSmall() {
        return coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
    }

    public String getGoodsCoverThumbMid() {
        return coreUploadService().thumb(this.siteId, this.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
    }
}
