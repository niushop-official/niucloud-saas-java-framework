package com.niu.shop.service.admin.goods.param;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import jakarta.validation.constraints.*;

/**
 * 商品参数
*/
@Data
public class ShopGoodsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "goods_name参数缺失")
    @NotEmpty(message = "商品名称不能为空")
    private String goodsName;

    @NotNull(message = "sub_title参数缺失")
    private String subTitle;

    @NotNull(message = "goods_type参数缺失")
    @NotEmpty(message = "商品类型不能为空")
    private String goodsType;

    @NotNull(message = "goods_image参数缺失")
    @NotNull(message = "商品图片不能为空")
    private String goodsImage;

    private String goodsVideo = "";

    @NotNull(message = "goods_category参数缺失")
    private Integer[] goodsCategory;

    @NotNull(message = "goods_desc参数缺失")
    @NotNull(message = "商品详情不能为空")
    private String goodsDesc;

    private Integer brandId = 0;

    @NotNull(message = "labelIds参数缺失")
    private Integer[] labelIds;

    @NotNull(message = "serviceIds参数缺失")
    private Integer[] serviceIds;

    private Integer supplierId = 0;

    @NotNull(message = "status参数缺失")
    private Integer status;

    private Integer sort = 0;

    private Integer attrId = 0;

    @NotNull(message = "attr_format参数缺失")
    private String attrFormat;

    private Integer isGift = 0;

    @NotNull(message = "spec_type参数缺失")
    private String specType;

    private BigDecimal price = new BigDecimal(0);

    private BigDecimal marketPrice = new BigDecimal(0);

    private BigDecimal costPrice = new BigDecimal(0);

    private BigDecimal weight = new BigDecimal(0);

    private BigDecimal volume = new BigDecimal(0);

    private Integer stock = 0;

    @NotNull(message = "sku_no参数缺失")
    private String skuNo;

    @NotNull(message = "unit参数缺失")
    private String unit = "件";

    private Integer virtualSaleNum = 0;

    private Integer isLimit = 0;

    private Integer limitType = 1;

    private Integer maxBuy = 0;

    private Integer minBuy = 0;


    @NotNull(message = "goods_sku_data参数缺失")
    private Map<String, SkuData> goodsSkuData;

    @NotNull(message = "goods_spec_format参数缺失")
    private JSONArray goodsSpecFormat;

    @NotNull(message = "delivery_type参数缺失")
    private String[] deliveryType;

    private Integer isFreeShipping = 0;

    @NotNull(message = "fee_type参数缺失")
    private String feeType = "";

    private BigDecimal deliveryMoney = new BigDecimal(0);

    private Integer deliveryTemplateId = 0;

    @NotNull(message = "member_discount参数缺失")
    private String memberDiscount;

    private Integer posterId = 0;

    private Integer formId = 0;

    @Data
    public static class SkuData {
        private Integer skuId;

        @NotNull(message = "sku_no参数缺失")
        private String skuNo;

        @NotNull(message = "sku_image参数缺失")
        private String skuImage;

        @NotNull(message = "price参数缺失")
        private BigDecimal price = new BigDecimal(0);

        private BigDecimal marketPrice = new BigDecimal(0);

        private BigDecimal costPrice = new BigDecimal(0);

        private BigDecimal weight = new BigDecimal(0);

        private BigDecimal volume = new BigDecimal(0);

        @NotNull(message = "stock参数缺失")
        private Integer stock;

        @NotNull(message = "sku_spec参数缺失")
        private JSONArray skuSpec;

        @NotNull(message = "spec_name参数缺失")
        private String specName;

        @NotNull(message = "is_default参数缺失")
        private Integer isDefault = 0;
    }
}
