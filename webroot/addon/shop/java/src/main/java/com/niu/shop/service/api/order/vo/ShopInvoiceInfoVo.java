package com.niu.shop.service.api.order.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.enums.order.InvoiceHeaderTypeEnum;
import com.niu.shop.enums.order.InvoiceTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ShopInvoice视图
 */
@Data
public class ShopInvoiceInfoVo extends ShopInvoiceListVo {
    private List<OrderList> orderList;

    @Data
    public static class OrderList{
        private Integer invoiceId;
        private Integer orderId;
        private String orderNo;
        private BigDecimal orderMoney;
        private List<OrderGoods> orderGoods;
    }

    @Data
    public static class OrderGoods{
        @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
        private String extend;
        private Integer orderGoodsId;
        private Integer siteId;
        private Integer orderId;
        private Integer memberId;
        private Integer goodsId;
        private Integer skuId;
        private String goodsName;
        private String skuName;
        private String goodsImage;
        private String skuImage;
        private BigDecimal price;
        private Integer num;
        private BigDecimal goodsMoney;
        private Integer isEnableRefund;
        private Integer deliveryId;
        private Integer status;
        private Integer isGift;
        private OrderDelivery orderDelivery;
        private String goodsImageThumbSmall;

        public String getGoodsImageThumbSmall(){
            if (StringUtils.isEmpty(this.goodsImage)){
                return "";
            }
            return CommonUtils.thumbImageSmall(this.siteId, this.goodsImage);
        }
    }

    @Data
    public static class OrderDelivery{
        private Integer id;
        private Integer expressCompanyId;
        private String expressNumber;
        private ShopDeliveryCompany company;
    }
}
