package com.niu.shop.entity.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ShopCouponSendRecords {

    /**
     * 主键id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 优惠券id
     */
    private Integer couponId;

    /**
     * 每位会员发放数量
     */
    private Integer sendNum;

    /**
     * 发券范围
     */
    private String rangeType;

    /**
     * 范围对应参数
     */
    private String rangeParam;

    /**
     * 发放成功数
     */
    private Integer successNum;

    /**
     * 状态 wait-待发送 process-发送中 finish-结束
     */
    private String status;

    /**
     * 发放会员数
     */
    private Integer memberNum;

    /**
     * 发放结束时间
     */
    private Long endTime;

    /**
     * 操作人id
     */
    private Integer adminUid;

    /**
     * 操作人名称
     */
    private String adminUsername;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

}
