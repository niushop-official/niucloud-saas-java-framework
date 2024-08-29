package com.niu.core.service.admin.pay.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 支付渠道配置参数
*/
@Data
public class PayChannelParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "channel参数缺失")
    private String channel;

    @NotNull(message = "config参数缺失")
    private String config;

    @NotNull(message = "status参数缺失")
    private Integer status;

    @NotNull(message = "sort参数缺失")
    private Integer sort;

}
