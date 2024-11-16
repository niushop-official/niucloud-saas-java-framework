package com.niu.shop.service.admin.marketing.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 积分兑换参数
 */
@Data
public class ShopPointExchangeSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String type;

    private String names;

    private String title;

    private String image;

    private Integer status;

    private String productDetail;

    private Integer point;

    private BigDecimal price;

    private Integer limitNum;

    private String content;

    private Integer sort;

    private Integer totalPointNum;

    private Integer totalOrderNum;

    private Integer totalMemberNum;

    private Integer stock;

    private Integer totalExchangeNum;

    private String[] createTime;

}
