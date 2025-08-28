package com.niu.shop.service.admin.marketing.param;

import cn.hutool.json.JSONArray;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 限时折扣参数
*/
@Data
public class ShopDiscountParam implements Serializable {

    private static final long serialVersionUID = 1L;


    @NotNull(message = "限时折扣名称参数缺失")
    private String name;

    private String remark;

    @NotEmpty(message = "startTime参数缺失")
    private String startTime;

    @NotEmpty(message = "endTime参数缺失")
    private String endTime;

    private List<GoodsItemParam> goodsList;

    @Data
    public static class GoodsItemParam {
        private Integer goodsId;
        private List<SkuItemParam> skuList;
    }

    @Data
    public static class SkuItemParam {
        private Integer skuId;
        private Integer goodsId;
        private Integer isEnabled;
        private String discountType;
        private BigDecimal discountPrice;
        private BigDecimal discountRate;
        private BigDecimal reduceMoney;
        private BigDecimal specifyPrice;
    }

}
