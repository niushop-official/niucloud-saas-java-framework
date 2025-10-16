package com.niu.shop.service.admin.goods.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
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
    private String subTitle;
    private String goodsType;  // 商品类型
    private String goodsCover;  // 商品封面
    private String goodsCoverThumbSmall;
    private Integer saleNum;  // 销量
    private Integer status;  // 商品状态（1.正常0下架）
    private Integer stock;
    private Integer sort;  // 排序
    private String goodsTypeName;
    private String goodsEditPath;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    private String memberDiscount;
    private Integer isGift;
    @JsonProperty("goodsSku")
    private GoodsSku goodsSku;
    private Map<String, Map<String, Object>> active;

    @Data
    public static class GoodsSku {
        private Integer goodsId;
        private Integer skuId;
        private BigDecimal price;
        private String memberPrice;
    }

    public String getGoodsTypeName() {
        return GoodsTypeEnum.getNameByType(this.goodsType);
    }

    public String getGoodsEditPath() {
        Map<String, Map<String, Object>> map = GoodsTypeEnum.getMap();
        return map.get(this.goodsType).get("path").toString();
    }
}
