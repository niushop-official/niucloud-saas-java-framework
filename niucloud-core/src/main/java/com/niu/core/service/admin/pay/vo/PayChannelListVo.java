package com.niu.core.service.admin.pay.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * PayChannel列表视图
 */
@Data
public class PayChannelListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private Integer siteId;  // 站点id
    private String type;  // 支付类型
    private String channel;  // 支付渠道
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String config;  // 支付配置
    private String createTime; // 创建时间
    private String updateTime; // 修改时间
    private Integer status;  // 是否启用
    private Integer sort;  // 排序

}
