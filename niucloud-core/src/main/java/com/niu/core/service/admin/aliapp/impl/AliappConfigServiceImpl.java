package com.niu.core.service.admin.aliapp.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.aliapp.IAliappConfigService;
import com.niu.core.service.core.aliapp.ICoreAliappConfigService;
import com.niu.core.service.core.aliapp.param.AliappConfigParam;
import com.niu.core.service.core.aliapp.vo.AliappConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AliappConfigServiceImpl implements IAliappConfigService {

    @Resource
    ICoreAliappConfigService coreAliappConfigService;

    @Override
    public AliappConfigVo getAliappConfig() {
        return coreAliappConfigService.getAliappConfig(RequestUtils.siteId());
    }

    @Override
    public void setAliappConfig(AliappConfigParam aliappConfigParam) {
        coreAliappConfigService.setAliappConfig(RequestUtils.siteId(), aliappConfigParam);

    }

    @Override
    public Map<String, String> getStatic() {
        Map<String, String> map = new HashMap<>();
        map.put("domain", RequestUtils.handler().getServerName());
        return map;
    }
}
