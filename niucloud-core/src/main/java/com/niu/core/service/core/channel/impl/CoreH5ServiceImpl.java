package com.niu.core.service.core.channel.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.channel.ICoreH5Service;
import com.niu.core.service.core.channel.param.SetH5Param;
import com.niu.core.service.core.channel.vo.H5ConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class CoreH5ServiceImpl implements ICoreH5Service {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public H5ConfigVo getH5(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "h5");
        H5ConfigVo vo = new H5ConfigVo();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            vo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), H5ConfigVo.class);
        }
        return vo;
    }

    @Override
    public void setH5(SetH5Param param) {
        JSONObject json = JSONUtil.parseObj(param);
        coreConfigService.setConfig(RequestUtils.siteId(), "h5", json);
    }
}
