package com.niu.core.service.admin.site.param;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 站点账单记录参数
 */
@Data
public class SiteAccountLogSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;

    private String tradeNo;

    private String[] createTime;

}
