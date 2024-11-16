package com.niu.shop.service.admin.stat.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 【请填写功能名称】参数
 */
@Data
public class ShopStatSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String date;

    private Integer dateTime;

    private Integer orderNum;

    private BigDecimal saleMoney;

    private BigDecimal refundMoney;

    private Integer accessSum;

}
