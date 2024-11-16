package com.niu.shop.service.admin.goods.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ShopGoods视图
 */
@Data
public class ShopGoodsInitVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private GoodsInfo goodsInfo;

    @Data
    public static class GoodsInfo {
        private Integer goodsId;  // 商品id
        private String goodsName;  // 商品名称
        private String goodsType;  // 商品类型
        private String subTitle;  // 副标题
        private String goodsCover;  // 商品封面
        private String goodsImage;  // 商品图片
        private Integer[] goodsCategory;  // 商品分类
        private String goodsDesc;  // 商品介绍
        private Object brandId;  // 商品品牌id
        private Integer[] labelIds;  // 标签组
        private Integer[] serviceIds;  // 商品服务
        private String unit;  // 单位
        private Integer stock;  // 商品库存（总和）
        private Integer virtualSaleNum;  // 虚拟销量
        private String status;  // 商品状态（1.正常0下架）
        private Integer sort;  // 排序
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String deliveryType;  // 支持的配送方式
        private Integer isFreeShipping;  // 是否免邮
        private String feeType;  // 运费设置，选择模板：template，固定运费：fixed
        private BigDecimal deliveryMoney;  // 固定运费
        private Integer deliveryTemplateId;  // 运费模板
        private Object supplierId;  // 供应商id
        private Object attrId;  // 商品参数id
        private String attrFormat;  // 商品参数内容，json格式
        private String memberDiscount;  // 会员等级折扣，不参与：空，会员折扣：discount，指定会员价：fixed_price
        private Object posterId;  // 海报id
        private List<SkuInfo> skuList;
        private String specType = "single";
        private List<SpecInfo> specList;
        private Long activeGoodsCount;
    }

    @Data
    public static class SkuInfo {
        private Integer skuId;  // 商品sku_id
        private Integer siteId;  // 站点id
        private String skuName;  // 商品sku名称
        private String skuImage;  // sku主图
        private String skuNo;  // 商品sku编码
        private Integer goodsId;  // 商品id
        private String skuSpecFormat;  // sku规格格式
        private BigDecimal price;  // sku单价
        private BigDecimal marketPrice;  // 划线价
        private BigDecimal salePrice;  // 实际卖价（有活动显示活动价，默认原价）
        private BigDecimal costPrice;  // sku成本价
        private Integer stock;  // 商品sku库存
        private BigDecimal weight;  // 重量（单位kg）
        private BigDecimal volume;  // 体积（单位立方米）
        private Integer isDefault;  // 是否默认
    }

    @Data
    public static class SpecInfo {
        private Integer specId;  // 规格id
        private Integer goodsId;  // 关联商品id
        private String specName;  // 规格项名称
        private String specValues;  // 规格值名称，多个逗号隔开
    }
}
