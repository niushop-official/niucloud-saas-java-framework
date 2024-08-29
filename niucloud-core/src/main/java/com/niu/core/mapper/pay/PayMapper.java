package com.niu.core.mapper.pay;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.pay.Pay;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录 Mapper
 */
@Mapper
public interface PayMapper extends MPJBaseMapper<Pay> {
}
