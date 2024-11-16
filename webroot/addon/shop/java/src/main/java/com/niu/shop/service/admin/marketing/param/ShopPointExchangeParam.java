package com.niu.shop.service.admin.marketing.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 积分兑换参数
 */
@Data
public class ShopPointExchangeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;

    private String stock;

    private Integer point;

    private BigDecimal price;

    private Integer limitNum;

    @NotNull(message = "productDetail参数缺失")
    private String productDetail;

    private Integer siteId;

    @NotNull(message = "names参数缺失")
    private String names;

    private String title;

    @NotNull(message = "image参数缺失")
    private String image;

    private Integer status;

    private String content;

    private Integer sort;

    private Integer totalPointNum;

    private BigDecimal totalPriceNum;

    private Integer totalOrderNum;

    private Integer totalMemberNum;

    private Integer totalExchangeNum;

}
