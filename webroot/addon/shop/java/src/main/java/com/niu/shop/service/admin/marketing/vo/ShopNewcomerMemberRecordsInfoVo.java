package com.niu.shop.service.admin.marketing.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 新人专享会员参与记录表详情
 */
@Data
public class ShopNewcomerMemberRecordsInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 参与时间
     */
    private Long createTime;


    /**
     * 参与商品id集合
     */
    private String goodsIds;


    /**
     * 是否参与
     */
    private Integer isJoin;


    /**
     * 会员id
     */
    private Integer memberId;


    /**
     * 参与订单id
     */
    private Integer orderId;


    /**
     * 主键id
     */
    private Integer recordId;


    /**
     * 站点id
     */
    private Integer siteId;


    /**
     * 参与商品规格id集合
     */
    private String skuIds;


    /**
     * 更新时间
     */
    private Long updateTime;


    /**
     * 有效期
     */
    private Long validityTime;

}