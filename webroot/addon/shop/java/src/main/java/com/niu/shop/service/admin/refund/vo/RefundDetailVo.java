package com.niu.shop.service.admin.refund.vo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.enums.order.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ShopOrderRefund列表视图
 */
@Data
public class RefundDetailVo implements Serializable {

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
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String voucher;  // 凭证
    private String source;  // 来源 system 系统 member 会员
    private Integer timeout;  // 操作超时时间
    private String refundNo;  // 退款交易号
    private Object delivery;  // 退货配送信息
    private String shopReason;  // 上架拒绝原因
    private String refundAddress;  // 商家退货地址
    private Integer isRefundDelivery;  // 是否退运费
    private String statusName;
    private String refundTypeName;
    private OrderGoods orderGoods;
    private List<RefundLog> refundLog;
    private MemberBriefInfoVo member;
    private OrderMain orderMain;
    private PayRefund payRefund;

    public String getStatusName() {
        return OrderRefundStatusEnum.getNameByStatus(Integer.valueOf(this.status));
    }

    public String getRefundTypeName() {
        return OrderRefundTypeEnum.getNameByType(Integer.valueOf(this.refundType));
    }

    @Data
    public static class Delivery {
        private String expressCompany;
        private String expressNumber;
        private String remark;
    }

    @Data
    public static class OrderMain {
        private Integer orderId;  //
        private Integer siteId;  // 站点id
        private String orderNo;  // 订单编号
        private String body;  // 订单内容
        private String orderType;  // 订单类型
        private String orderTypeName;// 订单类型
        private String orderFrom;  // 订单来源
        private String orderFromName;
        private String outTradeNo;  // 支付流水号
        private String status;  // 订单状态
        private Map<String, Object> statusName;
        private BigDecimal goodsMoney;  // 商品金额
        private BigDecimal deliveryMoney;  // 配送金额
        private BigDecimal discountMoney;  // 优惠金额
        private BigDecimal orderMoney;  // 订单金额
        private BigDecimal payMoney;  // 支付金额
        private Integer invoiceId;  // 发票id，0表示不开发票
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long payTime;  // 订单支付时间
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long deliveryTime;  // 订单发货时间
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long takeTime;  // 订单收货时间
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long finishTime;  // 订单完成时间
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long closeTime;  // 订单关闭时间
        private String deliveryType;  // 配送方式
        private String deliveryTypeName;
        private Integer takeStoreId;  // 自提点
        private String takerName;  // 收货人
        private String takerMobile;  // 收货人手机号
        private Integer takerProvince;  // 收货省
        private Integer takerCity;  // 收货市
        private Integer takerDistrict;  // 收货区县
        private String takerAddress;  // 收货地址
        private String takerFullAddress;  // 收货详细地址
        private String takerLongitude;  // 收货地址经度
        private String takerLatitude;  // 收货详细纬度
        private String takerStoreId;  // 收货门店
        private Integer isEnableRefund;  // 是否允许退款
        private String memberRemark;  // 会员留言信息
        private String shopRemark;  // 商家留言
        private Integer refundStatus;  // 退款状态  1不存在退款  2 部分退款  3 全部退款

        public String getOrderTypeName() {
            return "";
        }

        public String getOrderFromName() {
            return ChannelEnum.getNameByCode(this.orderFrom);
        }

        public String getDeliveryTypeName() {
            return OrderDeliveryTypeEnum.getNameByType(this.deliveryType);
        }

        public Map<String, Object> getStatusName() {
            return OrderStatusEnum.getMapByStatus(Integer.valueOf(this.status));
        }
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

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsImageThumbSmall() {
            return CommonUtils.thumbImageSmall(this.siteId, this.getGoodsImage());
        }
    }

    @Data
    public static class RefundLog {
        private String orderRefundNo; // 退款编号
        private String mainType; //
        private String mainTypeName;
        private Integer mainId; // 操作人id
        private Integer status; // 退款状态
        private String type; //
        private String typeName;
        private String content; // 日志内容
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime; // 创建时间

        public String getMainTypeName() {
            return OrderLogMainTypeEnum.getNameByType(this.mainType);
        }

        public String getTypeName() {
            return OrderRefundActionTypeEnum.getNameByType(this.type);
        }

        private MemberMapper memberMapper() {
            return SpringContext.bean(MemberMapper.class);
        }

        private SysUserMapper userMapper() {
            return SpringContext.bean(SysUserMapper.class);
        }

        public String getMainName() {
            if (this.mainType.equals(OrderLogMainTypeEnum.MEMBER.getType())) {
                Member member = memberMapper().selectOne(new QueryWrapper<Member>().eq("member_id", this.mainId).select("nickname"));
                if (member != null) return member.getNickname();
            }
            if (this.mainType.equals(OrderLogMainTypeEnum.STORE.getType())) {
                SysUser user = userMapper().selectOne(new QueryWrapper<SysUser>().eq("uid", this.mainId).select("username"));
                if (user != null) return user.getUsername();
            }
            return "";
        }
    }

    public static class PayRefund {

        private String refundNo;

        private String type;

        private String typeName;

        private BigDecimal money;

        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;

        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long refundTime;

        public String getTypeName() {
            return PayTypeEnum.getType().getByPath(this.type + ".name", String.class);
        }
    }
}
