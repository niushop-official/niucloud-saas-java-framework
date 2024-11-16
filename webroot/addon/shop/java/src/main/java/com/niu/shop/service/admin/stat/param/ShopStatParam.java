package com.niu.shop.service.admin.stat.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 【请填写功能名称】参数
*/
@Data
public class ShopStatParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "date参数缺失")
    private String date;

    @NotNull(message = "dateTime参数缺失")
    private Integer dateTime;

    @NotNull(message = "orderNum参数缺失")
    private Integer orderNum;

    @NotNull(message = "saleMoney参数缺失")
    private BigDecimal saleMoney;

    @NotNull(message = "refundMoney参数缺失")
    private BigDecimal refundMoney;

    @NotNull(message = "accessSum参数缺失")
    private Integer accessSum;

}
