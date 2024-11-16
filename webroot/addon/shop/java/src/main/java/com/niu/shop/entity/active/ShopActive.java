package com.niu.shop.entity.active;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 店铺营销活动（整体活动）实体
 */
@Data
public class ShopActive implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="active_id", type= IdType.AUTO)
    private Integer activeId; // 活动id
    private Integer siteId; // 站点id
    private String activeName; // 活动名称
    private String activeDesc; // 活动说明
    private String activeType; // 活动类型(店铺活动，会员活动，商品活动)
    private String activeGoodsType; // 商品活动类型（单品，独立商品，店铺整体商品）
    private String activeGoodsInfo; // 参与活动商品信息
    private String activeClass; // 活动类别
    private String activeClassCategory; // 活动类别子分类（活动管理）
    private String relateMember; // 参与会员条件(默认全部)
    private String activeValue; // 活动扩展信息数据
    private Long startTime; // 活动开始时间
    private Long endTime; // 活动结束时间
    private String activeStatus; // 活动状态
    private Long createTime; // 添加时间
    private Long updateTime; // 修改时间
    private BigDecimal activeOrderMoney; // 活动累计金额
    private Integer activeOrderNum; // 活动累计订单数
    private Integer activeMemberNum; // 活动参与会员数
    private Integer activeSuccessNum; // 活动成功参与会员数

}