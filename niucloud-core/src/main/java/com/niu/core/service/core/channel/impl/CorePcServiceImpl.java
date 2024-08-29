package com.niu.core.service.core.channel.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.aliapp.ICoreAliappConfigService;
import com.niu.core.service.core.aliapp.param.AliappConfigParam;
import com.niu.core.service.core.aliapp.vo.AliappConfigVo;
import com.niu.core.service.core.channel.ICorePcService;
import com.niu.core.service.core.channel.param.SetPcParam;
import com.niu.core.service.core.channel.vo.PcConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CorePcServiceImpl implements ICorePcService {

    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 获取pc端配置
     *
     * @param siteId
     * @return
     */
    @Override
    public PcConfigVo getPc(Integer siteId) {
        CoreSysConfigVo coreSysConfigVo = coreConfigService.getConfig(siteId, "pc");
        PcConfigVo vo = new PcConfigVo();
        if(ObjectUtil.isNotNull(coreSysConfigVo.getValueJson()))
        {
            vo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), PcConfigVo.class);
        }
        return vo;
    }

    @Override
    public void setPc(SetPcParam param) {
        JSONObject json = JSONUtil.parseObj(param);
        coreConfigService.setConfig(RequestUtils.siteId(), "pc", json);
    }
}
