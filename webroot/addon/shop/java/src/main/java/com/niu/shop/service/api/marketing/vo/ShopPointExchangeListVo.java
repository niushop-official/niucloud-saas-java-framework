package com.niu.shop.service.api.marketing.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopPointExchange列表视图
 */
@Data
public class ShopPointExchangeListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 兑换活动主键id
    private Integer siteId;  // 站点id
    private String type;  // 兑换类型（商品、优惠券、红包）
    private String typeName;  // 兑换类型名称
    private String names;  // 兑换标题
    private String title;  // 副标题
    private String image;  // 图片
    private Integer status;  // 兑换状态 0 下架  1上架  -1 删除
    private String statusName;  // 兑换状态 0 下架  1上架  -1 删除
    private String productDetail;  // 兑换产品信息
    private Integer point;  // 兑换所需积分
    private BigDecimal price;  // 兑换所需金额
    private Integer limitNum;  // 限制数量
    private Integer sort;  // 排序
    private Integer totalPointNum;  // 积分消费总额
    private BigDecimal totalPriceNum;  // 总支付金额
    private Integer totalOrderNum;  // 订单笔数
    private Integer totalMemberNum;  // 参与会员数
    private String updateTime; // 更新时间
    private String createTime; // 创建时间
    private String goodsCoverThumbBig; // 封面缩略图（大）
    private String goodsCoverThumbSmall; // 封面缩略图（小）
    private String goodsCoverThumbMid; // 封面缩略图（小）
    private Integer stock;  // 库存
    private Integer totalExchangeNum;  // 兑换数量

}
