package com.niu.core.service.admin.site.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

/**
 * 站点账单记录参数
*/
@Data
public class SiteAccountLogParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "money参数缺失")
    private BigDecimal money;

    @NotNull(message = "tradeNo参数缺失")
    private String tradeNo;

}
