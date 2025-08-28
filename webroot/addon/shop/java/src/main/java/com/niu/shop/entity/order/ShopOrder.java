package com.niu.shop.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 订单实体
 */
@Data
public class ShopOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="order_id", type= IdType.AUTO)
    private Integer orderId; // 
    private Integer siteId; // 站点id
    private String orderNo; // 订单编号
    private String body; // 订单内容
    private String orderType; // 订单类型
    private String orderFrom; // 订单来源
    private String outTradeNo; // 支付流水号
    private String status; // 订单状态
    private Integer memberId; // 会员id
    private String ip; // ip
    private BigDecimal goodsMoney; // 商品金额
    private BigDecimal deliveryMoney; // 配送金额
    private BigDecimal discountMoney; // 优惠金额
    private BigDecimal orderMoney; // 订单金额
    private BigDecimal payMoney; // 支付金额
    private Integer invoiceId; // 发票id，0表示不开发票
    private Long createTime; // 创建时间
    private Long payTime; // 订单支付时间
    private Long deliveryTime; // 订单发货时间
    private Long takeTime; // 订单收货时间
    private Long finishTime; // 订单完成时间
    private Long closeTime; // 订单关闭时间
    @TableLogic
    private Long deleteTime; // 是否删除(针对后台)
    private Long timeout; // 通用业务超时时间记录
    private String deliveryType; // 配送方式
    private Integer takeStoreId; // 自提点
    private String takerName; // 收货人
    private String takerMobile; // 收货人手机号
    private Integer takerProvince; // 收货省
    private Integer takerCity; // 收货市
    private Integer takerDistrict; // 收货区县
    private String takerAddress; // 收货地址
    private String takerFullAddress; // 收货详细地址
    private String takerLongitude; // 收货地址经度
    private String takerLatitude; // 收货详细纬度
    private String takerStoreId; // 收货门店
    private Integer isEnableRefund; // 是否允许退款
    private String memberRemark; // 会员留言信息
    private String shopRemark; // 商家留言
    private String closeRemark; // 关闭原因
    private String closeType; // 关闭来源(未支付自动关闭   手动关闭  退款关闭)
    private Integer refundStatus; // 退款状态  1不存在退款  2 部分退款  3 全部退款
    private String hasGoodsTypes; // 包含的商品类型 json
    private Integer isEvaluate; // 是否评论
    private Integer relateId; // 关联id
    private Integer point; // 积分兑换
    private String activityType; // 营销类型
    private Integer formRecordId;
    private String buyerAskDeliveryTime;

}