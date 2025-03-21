package com.niu.core.service.core.wxoplatform.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.mapper.sys.SysConfigMapper;
import com.niu.core.service.core.wxoplatform.ICoreOplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class CoreOplatformServiceImpl implements ICoreOplatformService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    /**
     * 根据appid获取站点id
     * @param appid
     * @return
     */
    @Override
    public Integer getSiteIdByAuthorizerAppid(String appid) {
        String[] configKey = new String[]{ ConfigKeyEnum.WECHAT.getName(), ConfigKeyEnum.WEAPP.getName(), ConfigKeyEnum.WEAPP_AUTHORIZATION_INFO.getName(), ConfigKeyEnum.WECHAT_AUTHORIZATION_INFO.getName()};
        SysConfig config = sysConfigMapper.selectOne(new QueryWrapper<SysConfig>().like("value", appid).in("config_key", configKey).last("limit 1"));
        return config == null ? 0 : config.getSiteId();
    }
}
