package com.niu.core.service.core.aliapp.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.aliapp.ICoreAliappConfigService;
import com.niu.core.service.core.aliapp.param.AliappConfigParam;
import com.niu.core.service.core.aliapp.vo.AliappConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreAliappConfigServiceImpl  implements ICoreAliappConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public AliappConfigVo getAliappConfig(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "aliapp");
        AliappConfigVo vo = new AliappConfigVo();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            vo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), AliappConfigVo.class);
        }
        return vo;
    }

    @Override
    public void setAliappConfig(Integer siteId, AliappConfigParam aliappConfigParam) {
        JSONObject json = JSONUtil.parseObj(aliappConfigParam);
        coreConfigService.setConfig(siteId, "aliapp", json);

    }
}
