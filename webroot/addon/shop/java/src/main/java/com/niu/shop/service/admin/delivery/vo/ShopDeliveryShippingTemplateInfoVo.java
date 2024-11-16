package com.niu.shop.service.admin.delivery.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ShopDeliveryShippingTemplate视图
 */
@Data
public class ShopDeliveryShippingTemplateInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer templateId;  // 
    private Integer siteId;  // 站点id
    private String templateName;  // 模板名称
    private String feeType;  // 运费计算方式1.重量2体积3按件
    private Integer isFreeShipping;  // 该区域是否包邮
    private Integer noDelivery;  // 是否指定该区域不配送
    private List<FeeData> feeData;
    private List<NoDeliveryData> noDeliveryData;
    private List<FreeShippingData> freeShippingData;

    @Data
    public static class FeeData {
        private String feeAreaIds; // 运费设置区域id集
        private String feeAreaNames; // 运费设置区域名称集
        private String[] areaIds; // 运费设置区域id集
        private Integer snum; // 起步计算标准
        private BigDecimal sprice; // 起步计算价格
        private Integer xnum; // 续步计算标准
        private BigDecimal xprice; // 续步计算价格

        public String[] getAreaIds() {
            return this.feeAreaIds.split(",");
        }
    }

    @Data
    public static class NoDeliveryData {
        private String noDeliveryAreaIds; // 不配送的区域id集
        private String[] areaIds; // 不配送的区域id集
        private String noDeliveryAreaNames; // 不配送的区域名称集

        public String[] getAreaIds() {
            return this.noDeliveryAreaIds.split(",");
        }
    }

    @Data
    public static class FreeShippingData {
        private String freeShippingAreaIds; // 包邮的区域id集
        private String[] areaIds; // 包邮的区域id集
        private String freeShippingAreaNames; // 包邮的区域名称集
        private BigDecimal freeShippingPrice; // 满足包邮的条件
        private Integer freeShippingNum; //

        public String[] getAreaIds() {
            return this.freeShippingAreaIds.split(",");
        }
    }
}
