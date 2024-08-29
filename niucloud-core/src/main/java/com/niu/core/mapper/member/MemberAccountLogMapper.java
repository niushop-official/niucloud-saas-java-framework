package com.niu.core.mapper.member;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.member.MemberAccountLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 会员账单 Mapper
 */
@Mapper
public interface MemberAccountLogMapper extends MPJBaseMapper<MemberAccountLog> {

    Double sumAccountData(Map<String, Object> paramsMap);

}
