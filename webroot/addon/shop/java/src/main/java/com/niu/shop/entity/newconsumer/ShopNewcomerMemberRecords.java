package com.niu.shop.entity.newconsumer;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 新人专享会员参与记录表实体
 */
@Data
public class ShopNewcomerMemberRecords implements Serializable {

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
    @TableId(value = "record_id", type = IdType.AUTO)
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