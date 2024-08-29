package com.niu.core.mapper.member;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.member.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员 Mapper
 */
@Mapper
public interface MemberMapper extends MPJBaseMapper<Member> {
}
