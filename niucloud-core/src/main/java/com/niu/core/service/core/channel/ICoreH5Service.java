package com.niu.core.service.core.channel;

import com.niu.core.service.core.channel.param.SetH5Param;
import com.niu.core.service.core.channel.vo.H5ConfigVo;

public interface ICoreH5Service {
    /**
     * 获取h5端配置
     * @return
     */
    H5ConfigVo getH5(Integer siteId);

    /**
     * 设置h5端配置
     * @param param
     */
    void setH5(SetH5Param param);

}
