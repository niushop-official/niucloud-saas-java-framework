package com.niu.recharge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="order_item_id", type= IdType.AUTO)
    private Integer orderItemId;

    /** 订单id */
    private Integer orderId;

    /** 站点id */
    private Integer siteId;

    /** 会员id */
    private Integer memberId;

    /** 项目id */
    private Integer itemId;

    /** 项目类型 */
    private String itemType;

    /** 项目名称 */
    private String itemName;

    /** 项目图片 */
    private String itemImage;

    /** 项目单价 */
    private BigDecimal price;

    /** 购买数量 */
    private Integer num;

    /** 项目总价 */
    private BigDecimal itemMoney;

    /** 是否退款 */
    private Integer isRefund;

    /** 退款编号 */
    private String refundNo;

    /** 退款状态 */
    private Integer refundStatus;

    /** 创建时间 */
    private Long createTime;

}
