package com.niu.shop.service.admin.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.enums.active.ActiveStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ShopActive视图
 */
@Data
public class ShopDiscountDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String statusName;
    private Integer discountId;
    private Integer siteId;
    private String name;
    private String remark;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long startTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long endTime;
    private String status;
    private BigDecimal orderMoney;
    private Integer orderNum;
    private Integer memberNum;
    private Integer successNum;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;
    private List<GoodsInfoVO> goodsInfo;

    public String getStatusName() {
        return ActiveStatusEnum.getNameByStatus(this.status);
    }

    @Data
    public static class GoodsInfoVO {
        private String goodsCoverThumbSmall;
        private Integer goodsId;
        private Integer skuId;
        private String type;
        private String goodsType;
        private String goodsTypeName;
        private String goodsName;
        private BigDecimal price;
        private BigDecimal rate;
        private BigDecimal reduceMoney;
        private BigDecimal discountPrice;
        private String specType;
        private List<GoodsSkuVO> goodsSkuList;

        // 多规格商品特有字段
        private BigDecimal minRate;
        private BigDecimal maxRate;
        private BigDecimal minReduceMoney;
        private BigDecimal maxReduceMoney;
        private BigDecimal minDiscountPrice;
        private BigDecimal maxDiscountPrice;

    }

    @Data
    public static class GoodsSkuVO {
        private String goodsCoverThumbSmall;
        private Integer discountGoodsId;
        private Integer siteId;
        private Integer discountId;
        private Integer goodsId;
        private Integer skuId;
        private String type;
        private BigDecimal rate;
        private BigDecimal reduceMoney;
        private BigDecimal discountPrice;
        private Integer isEnabled;
        private String goodsName;
        private String goodsCover;
        private String goodsType;
        private BigDecimal price;
        private String skuName;
        private String skuImage;
    }
}
