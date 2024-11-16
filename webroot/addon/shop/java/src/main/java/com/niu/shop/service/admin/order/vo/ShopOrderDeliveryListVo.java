package com.niu.shop.service.admin.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ShopOrderDelivery列表视图
 */
@Data
public class ShopOrderDeliveryListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // id
    private Integer siteId;  // 站点id
    private Integer orderId;  // 
    private String name;  // 包裹名称
    private String deliveryType;  // 配送方式
    private String subDeliveryType;  // 详细配送方式
    private Integer expressCompanyId;  // 快递公司id
    private String expressNumber;  // 配送单号
    private Integer localDeliverId;  // 同城配送员
    private Integer status;  // 配送状态
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    private String remark;
    private ShopDeliveryCompany company; //物流公司
    private List<ShopOrderDeliveryListVo.OrderGoods> orderGoods;

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
        private Integer verifyCount; // 已核销次数
        private Integer verifyExpireTime; // 过期时间 0 为永久
        private Integer isVerify; // 是否需要核销

        private ICoreUploadService coreUploadService() {
            return SpringContext.bean(ICoreUploadService.class);
        }

        public String getGoodsImageThumbSmall() {
            return coreUploadService().thumb(this.siteId, this.getGoodsImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
        }
    }



}
