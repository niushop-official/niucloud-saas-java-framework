package com.niu.core.mapper.pay;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.pay.PayChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付渠道配置 Mapper
 */
@Mapper
public interface PayChannelMapper extends MPJBaseMapper<PayChannel> {
}
