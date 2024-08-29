package com.niu.recharge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeOrderItemRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="refund_id", type= IdType.AUTO)
    private Integer refundId;

    private Integer orderItemId;

    /** 订单id */
    private Integer orderId;

    /** 订单编号 */
    private String orderNo;

    /** 退款编号 */
    private String refundNo;

    /** 站点id */
    private Integer siteId;

    /** 会员id */
    private Integer memberId;

    /** 退货数量 */
    private Integer num;

    /** 总退款 */
    private BigDecimal money;

    /** 退款状态 */
    private Integer status;

    /** 创建时间 */
    private Long createTime;

    /** 审核时间 */
    private Long auditTime;

    /** 转账时间 */
    private Long transferTime;

    /** 项目类型 */
    private String itemType;

}
