package com.niu.shop.service.admin.marketing.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.active.ActiveTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopActive视图
 */
@Data
public class ShopDiscountInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer activeId;  // 活动id
    private Integer siteId;  // 站点id
    private String activeName;  // 活动名称
    private String activeDesc;  // 活动说明
    private String activeType;  // 活动类型(店铺活动，会员活动，商品活动)
    private String activeGoodsType;  // 商品活动类型（单品，独立商品，店铺整体商品）
    private String activeGoodsInfo;  // 参与活动商品信息
    private String activeClass;  // 活动类别
    private String activeClassCategory;  // 活动类别子分类（活动管理）
    private String relateMember;  // 参与会员条件(默认全部)
    private String activeValue;  // 活动扩展信息数据
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long startTime; // 活动开始时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long endTime; // 活动结束时间
    private String activeStatus;  // 活动状态
    private BigDecimal activeOrderMoney;  // 活动累计金额
    private Integer activeOrderNum;  // 活动累计订单数
    private Integer activeMemberNum;  // 活动参与会员数
    private Integer activeSuccessNum;  // 活动成功参与会员数

    //新表
    private Integer discountId;
    private String name;
    private String remark;
    private String status;
    private String statusName;
    private BigDecimal orderMoney;
    private Integer orderNum;
    private Integer memberNum;
    private Integer successNum;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**
     * 获取活动类型
     * @return
     */
    public String getActiveTypeName() {
        return ActiveTypeEnum.getNameByType(this.activeType);
    }

    /**
     * 获取活动商品类型
     * @return
     */
    public String getActiveGoodsTypeName() {
        return ActiveGoodsTypeEnum.getNameByType(this.activeGoodsType);
    }

    /**
     * 获取活动状态
     * @return
     */
    public String getActiveStatusName() {
        return ActiveStatusEnum.getNameByStatus(this.activeStatus);
    }


    public String getStatusName() {
        return ActiveStatusEnum.getNameByStatus(this.status);
    }
}
