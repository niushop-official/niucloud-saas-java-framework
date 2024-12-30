package com.niu.shop.entity.manjian;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 满减活动表实体
 */
@Data
public class ShopManjian implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 条件类型 over_n_yuan:满N元  over_n_piece:满N件
     */
    private String conditionType;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 商品id集
     */
    private String goodsIds;

    /**
     * 参与商品 all_goods:全部商品参与  selected_goods:指定商品 selected_goods_not:指定商品不参与
     */
    private String goodsType;

    /**
     * 参与会员 all_member:所有会员参与  selected_member_level:指定会员等级  selected_member_label:指定会员标签
     */
    private String joinMemberType;

    /**
     * 会员标签id集
     */
    private String labelIds;

    /**
     * 会员等级id集
     */
    private String levelIds;

    /**
     * 满减活动id
     */
    @TableId(value = "manjian_id", type = IdType.AUTO)
    private Integer manjianId;

    /**
     * 名称
     */
    private String manjianName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 优惠规则json
     */
    private String ruleJson;

    /**
     * 优惠规格 ladder:阶梯优惠  cycle:循环优惠
     */
    private String ruleType;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 状态（0未开始1进行中2已结束-1已关闭）
     */
    private Integer status;

    /**
     * 活动累计赠送余额
     */
    private BigDecimal totalBalance;

    /**
     * 活动累计赠送优惠券数
     */
    private Integer totalCouponNum;

    /**
     * 活动累计赠送商品数
     */
    private Integer totalGoodsNum;

    /**
     * 活动参与会员数
     */
    private Integer totalMemberNum;

    /**
     * 活动累计金额
     */
    private BigDecimal totalOrderMoney;

    /**
     * 活动累计订单数
     */
    private Integer totalOrderNum;

    /**
     * 活动累计赠送积分
     */
    private Integer totalPoint;

    /**
     * 修改时间
     */
    private Long updateTime;

}