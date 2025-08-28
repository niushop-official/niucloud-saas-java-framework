package com.niu.shop.service.admin.refund.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.enums.order.OrderRefundStatusEnum;
import com.niu.shop.enums.order.OrderRefundTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopOrderRefund列表视图
 */
@Data
public class RefundListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer refundId;  // 
    private Integer siteId;  // 站点id
    private Integer orderId;  // 订单id
    private Integer orderGoodsId;  // 订单项id
    private String orderRefundNo;  // 退款单号
    private String refundType;  // 退款方式 
    private String reason;  // 退款原因 
    private Integer memberId;  // 会员id
    private BigDecimal applyMoney;  // 申请退款
    private BigDecimal money;  // 实际退款
    private String status;  // 退款状态
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long transferTime;  // 转账时间
    private String remark;  // 描述
    private String voucher;  // 凭证
    private String source;  // 来源 system 系统 member 会员
    private Integer timeout;  // 操作超时时间
    private String refundNo;  // 退款交易号
    private String delivery;  // 退货配送信息
    private String shopReason;  // 上架拒绝原因
    private String refundAddress;  // 商家退货地址
    private Integer isRefundDelivery;  // 是否退运费
    private String statusName;
    private String refundTypeName;
    private OrderGoods orderGoods;
    private MemberBriefInfoVo member;

    public String getStatusName() {
        return OrderRefundStatusEnum.getNameByStatus(Integer.valueOf(this.status));
    }

    public String getRefundTypeName() {
        return OrderRefundTypeEnum.getNameByType(Integer.valueOf(this.refundType));
    }

    @Data
    public static class OrderGoods {
        private Integer orderGoodsId; //
        private Integer siteId; // 站点id
        private Integer orderId; // 订单id
        private Integer memberId; // 购买会员id
        private Integer goodsId; // 商品id
        private Integer skuId; // 商品规格id
        private String goodsName; // 商品名称
        private String skuName; // 商品规格名称
        private String goodsImage; // 商品图片
        private String goodsImageThumbSmall;
        private String skuImage; // sku规格图片
        private BigDecimal price; // 商品单价
        private Integer num; // 购买数量
        private BigDecimal goodsMoney; // 商品总价
        private Integer isEnableRefund; // 是否允许退款
        private Integer deliveryId;
        private Integer status; // 状态
        private String orderRefundNo; // 退款单号
        private String goodsType; // 商品类型
        private String deliveryStatus; // 配送状态
        private BigDecimal discountMoney;


        public String getGoodsImageThumbSmall() {
            return CommonUtils.thumbImageSmall(this.siteId, this.getSkuImage());
        }
    }
}
