package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 订单参数
 */
@Data
public class ShopOrderSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String searchType;

    private String searchName;

    private Integer status;

    private String payType;

    private String orderFrom;

    private String[] createTime;

    private String[] payTime;

    private String activityType;

    private String keyword;
}
