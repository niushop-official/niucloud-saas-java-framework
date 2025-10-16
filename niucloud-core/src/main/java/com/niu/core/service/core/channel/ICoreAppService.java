package com.niu.core.service.core.channel;

import com.niu.core.service.core.channel.param.SetAppParam;
import com.niu.core.service.core.channel.vo.AppConfigVo;

/**
 * APP配置核心服务接口
 */
public interface ICoreAppService {

    /**
     * 获取APP配置
     * @param siteId 站点ID
     * @return AppConfigVo
     */
    AppConfigVo getConfig(Integer siteId);

    /**
     * 设置APP配置
     * @param siteId 站点ID
     * @param param APP配置参数
     * @return boolean
     */
    void setConfig(Integer siteId, SetAppParam param);
}