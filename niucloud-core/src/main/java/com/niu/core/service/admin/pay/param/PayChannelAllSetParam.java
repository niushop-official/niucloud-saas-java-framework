package com.niu.core.service.admin.pay.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 支付渠道配置参数
*/
@Data
public class PayChannelAllSetParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "config参数缺失")
    private JSONObject config;

}
