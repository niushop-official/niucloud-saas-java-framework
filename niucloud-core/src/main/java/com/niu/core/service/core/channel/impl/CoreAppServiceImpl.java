package com.niu.core.service.core.channel.impl;

import cn.hutool.json.JSONUtil;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.service.core.channel.ICoreAppService;
import com.niu.core.service.core.channel.param.SetAppParam;
import com.niu.core.service.core.channel.vo.AppConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * APP配置核心服务实现类
 */
@Service
public class CoreAppServiceImpl implements ICoreAppService {

    @Resource
    private ICoreConfigService coreConfigService;

    /**
     * 获取APP配置
     * @param siteId 站点ID
     * @return AppConfigVo
     */
    @Override
    public AppConfigVo getConfig(Integer siteId) {
        CoreSysConfigVo config = coreConfigService.getConfig(siteId, ConfigKeyEnum.APP.getName());
        if (config == null || config.getValueJson() == null) {
            return new AppConfigVo();
        }
        return JSONUtil.toBean(config.getValueJson(), AppConfigVo.class);
    }

    /**
     * 设置APP配置
     * @param siteId 站点ID
     * @param param APP配置参数
     * @return boolean
     */
    @Override
    public void setConfig(Integer siteId, SetAppParam param) {
        coreConfigService.setConfig(siteId, ConfigKeyEnum.APP.getName(), JSONUtil.parseObj(param));
    }
}