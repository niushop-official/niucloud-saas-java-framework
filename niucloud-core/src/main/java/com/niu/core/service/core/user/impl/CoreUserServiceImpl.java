package com.niu.core.service.core.user.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.core.user.ICoreUserService;
import com.niu.core.service.core.user.dto.UserInfoDto;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class CoreUserServiceImpl implements ICoreUserService {
    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private ISysUserRoleService userRoleService;
    @Override
    public UserInfoDto getUserInfo(Integer uid) {
        SysUser sysUser = sysUserMapper.selectById(uid);
        UserInfoDto result = new UserInfoDto();
        BeanUtils.copyProperties(sysUser, result);
        result.setRole(userRoleService.getUserRole(RequestUtils.siteId(), uid));
        return result;
    }
}
