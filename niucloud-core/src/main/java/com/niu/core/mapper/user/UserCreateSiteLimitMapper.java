package com.niu.core.mapper.user;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.entity.user.UserCreateSiteLimit;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户限制类 Mapper
 */
@Mapper
public interface UserCreateSiteLimitMapper extends MPJBaseMapper<UserCreateSiteLimit> {

}
