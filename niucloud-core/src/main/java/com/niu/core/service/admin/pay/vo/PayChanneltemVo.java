package com.niu.core.service.admin.pay.vo;
import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * PayChannel视图
 */
@Data
public class PayChanneltemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;  // 支付类型
    private String key;  // 支付渠道
    private JSONArray payType;
}
