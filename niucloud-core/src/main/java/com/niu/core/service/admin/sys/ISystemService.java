package com.niu.core.service.admin.sys;

import com.niu.core.service.admin.stat.vo.StatSystemVo;
import com.niu.core.service.admin.sys.param.SpreadQrcodeParam;
import com.niu.core.service.admin.sys.vo.SpreadQrcodeVo;

import java.util.Map;

public interface ISystemService {
    /**
     * 获取版权信息
     * @return
     */
    StatSystemVo getInfo();

    void clearCache();

    /**
     * 获取系统环境配置
     * @return
     */
    Map<String, Object> getSystemInfo();

    SpreadQrcodeVo getSpreadQrcode(SpreadQrcodeParam param);
}
