package com.niu.shop.service.admin.marketing.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ShopPointExchange视图
 */
@Data
public class ShopPointExchangeInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 兑换活动主键id
    private Integer siteId;  // 站点id
    private String type;  // 兑换类型（商品、优惠券、红包）
    private String typeName;  // 兑换类型名称（商品、优惠券、红包）
    private String names;  // 兑换标题
    private String title;  // 副标题
    private String image;  // 图片
    private Integer status;  // 兑换状态 0 下架  1上架  -1 删除
    private String productDetail;  // 兑换产品信息
    private Integer point;  // 兑换所需积分
    private BigDecimal price;  // 兑换所需金额
    private Integer limitNum;  // 限制数量
    private String content;  // 产品介绍
    private Integer sort;  // 排序
    private Integer totalPointNum;  // 积分消费总额
    private BigDecimal totalPriceNum;  // 总支付金额
    private Integer totalOrderNum;  // 订单笔数
    private Integer totalMemberNum;  // 参与会员数
    private Integer stock;  // 库存
    private Integer totalExchangeNum;  // 兑换数量
    private List<GoodsSku> goodsList;
    private GoodsInfo goodsInfo;

    @Data
    public static class GoodsSku {
        private String skuSpecFormat;
        private Integer skuId;
        private Integer siteId;
        private String skuName;
        private String skuImage;
        private String skuNo;
        private BigDecimal goodsPrice;
        private Integer goodsStock;
        private Integer isEnabled;
        private Integer goodsId;
        private Integer point;
        private BigDecimal price;
        private Integer limitNum;
        private Integer stock;
    }

    @Data
    public static class GoodsInfo {
        private Integer goodsId;
        private String goodsName;
        private String goodsImage;
        private String goodsCover;
        private String specType;
        private BigDecimal goodsPrice;
    }

}
