package com.niu.core.service.core.pay.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 支付渠道配置参数
 */
@Data
public class PayChannelSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String type;

    private String channel;

    private String config;

    private Integer status;

    private Integer sort;

}
