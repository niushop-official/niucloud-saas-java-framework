package com.niu.shop.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 订单项实体
 */
@Data
public class ShopOrderGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="order_goods_id", type= IdType.AUTO)
    private Integer orderGoodsId; // 
    private Integer siteId; // 站点id
    private Integer orderId; // 订单id
    private Integer memberId; // 购买会员id
    private Integer goodsId; // 商品id
    private Integer skuId; // 商品规格id
    private String goodsName; // 商品名称
    private String skuName; // 商品规格名称
    private String goodsImage; // 商品图片
    private String skuImage; // sku规格图片
    private BigDecimal price; // 商品单价
    private Integer num; // 购买数量
    private BigDecimal goodsMoney; // 商品总价
    private Integer isEnableRefund; // 是否允许退款
    private String goodsType; // 商品类型
    private String deliveryStatus; // 配送状态
    private Integer deliveryId; // 发货单号
    private BigDecimal discountMoney; // 优惠金额
    private Integer status; // 状态
    private String orderRefundNo; // 退款单号
    private BigDecimal orderGoodsMoney; // 订单项实付金额
    private BigDecimal originalPrice; // 商品原价
    private String extend; // 数据项扩展
    private Integer verifyCount; // 已核销次数
    private Long verifyExpireTime; // 过期时间 0 为永久
    private Integer isVerify; // 是否需要核销
    private Integer isGift; // 是否是赠品
    private Integer shopActiveRefund; //商家主动退款0：否 1：是
    private BigDecimal shopActiveRefundMoney; //商家主动退款金额

}