package com.niu.core.service.admin.sys;

import com.niu.core.service.admin.stat.vo.StatSystemVo;

public interface ISystemService {
    /**
     * 获取版权信息
     * @return
     */
    StatSystemVo getSystemInfo();

    void clearCache();
}
