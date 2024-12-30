package com.niu.shop.service.admin.goods.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GoodsStatRankInfo {

    @JsonProperty("goods_cover_thumb_small")
    private String goodsCoverThumbSmall;
    @JsonProperty("site_id")
    private Integer siteId;
    @JsonProperty("goods_id")
    private Integer goodsId;
    @JsonProperty("cart_num")
    private String cartNum;
    @JsonProperty("sale_num")
    private String saleNum;
    @JsonProperty("pay_num")
    private String payNum;
    @JsonProperty("pay_money")
    private String payMoney;
    @JsonProperty("refund_num")
    private String refundNum;
    @JsonProperty("refund_money")
    private String refundMoney;
    @JsonProperty("collect_num")
    private String collectNum;
    @JsonProperty("evaluate_num")
    private String evaluateNum;
    @JsonProperty("access_num")
    private String accessNum;
    @JsonProperty("goods_visit_member_count")
    private String goodsVisitMemberCount;
    @JsonProperty("goods_name")
    private String goodsName;
    @JsonProperty("goods_cover")
    private String goodsCover;
    @JsonProperty("is_gift")
    private Integer isGift;
}
