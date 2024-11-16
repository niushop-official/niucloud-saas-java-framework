package com.niu.shop.service.admin.goods.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsEvaluate视图
 */
@Data
public class ShopGoodsEvaluateInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer evaluateId;  // 
    private Integer siteId;  // 站点id
    private Integer orderId;  // 订单id
    private Integer orderGoodsId;  // 订单项ID
    private Integer goodsId;  // 商品ID
    private Integer memberId;  // 会员ID
    private String memberHead;  // 会员头像
    private String memberName;  // 会员名称
    private String content;  // 评价内容
    private String images;  // 评价图片
    private Integer isAnonymous;  // 1匿名  2不匿名
    private Integer scores;  // 评论分数 1-5
    private Integer isAudit;  // 审核状态 1待审 2通过 3拒绝
    private String explainFirst;  // 解释内容
    private Integer topping;  // 排序 置顶
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 评论时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime; // 修改时间

}
