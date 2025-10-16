package com.niu.core.service.core.user;

import com.niu.core.service.core.user.dto.UserInfoDto;

public interface ICoreUserService {
    /**
     * 获取当前登录用户详情
     * @param uid
     * @return
     */
    UserInfoDto getUserInfo(Integer uid);
}
