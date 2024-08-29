package com.niu.core.service.core.pay.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * PayChannel视图
 */
@Data
public class PayChannelInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private Integer siteId;  // 站点id
    private String type;  // 支付类型
    private String channel;  // 支付渠道
    private String config;  // 支付配置
    private Integer status;  // 是否启用
    private Integer sort;  // 排序

}
