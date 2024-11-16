package com.niu.shop.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 订单退款实体
 */
@Data
public class ShopOrderRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="refund_id", type= IdType.AUTO)
    private Integer refundId; // 
    private Integer siteId; // 站点id
    private Integer orderId; // 订单id
    private Integer orderGoodsId; // 订单项id
    private String orderRefundNo; // 退款单号
    private String refundType; // 退款方式 
    private String reason; // 退款原因 
    private Integer memberId; // 会员id
    private BigDecimal applyMoney; // 申请退款
    private BigDecimal money; // 实际退款
    private String status; // 退款状态
    private Long createTime; // 创建时间
    private Long transferTime; // 转账时间
    private String remark; // 描述
    private String voucher; // 凭证
    private String source; // 来源 system 系统 member 会员
    private Integer timeout; // 操作超时时间
    private String refundNo; // 退款交易号
    private String delivery; // 退货配送信息
    private String shopReason; // 上架拒绝原因
    private String refundAddress; // 商家退货地址
    private Integer isRefundDelivery; // 是否退运费

}