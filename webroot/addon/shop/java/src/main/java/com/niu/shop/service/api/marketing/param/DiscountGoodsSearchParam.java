package com.niu.shop.service.api.marketing.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 限时折扣参数
 */
@Data
public class DiscountGoodsSearchParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer discountId = 0;

    private String order;

    private String sort = "desc";

    private String keyword;

    private Object goodsCategory;

    private Integer brandId;

    private Object labelIds;

    private Object serviceIds;

    private BigDecimal startPrice;

    private BigDecimal endPrice;


}
