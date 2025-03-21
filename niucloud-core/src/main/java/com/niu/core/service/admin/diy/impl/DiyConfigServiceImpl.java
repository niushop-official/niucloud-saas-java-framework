package com.niu.core.service.admin.diy.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy.BottomConfigValue;
import com.niu.core.service.admin.diy.IDiyConfigService;
import com.niu.core.service.admin.diy.param.SetBottomConfigParam;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class DiyConfigServiceImpl implements IDiyConfigService {

    @Resource
    ICoreDiyConfigService coreDiyConfigService;

    @Override
    public List<BottomConfigVo> getBottomList() {
        return coreDiyConfigService.getBottomList();
    }

    @Override
    public BottomConfigVo getBottomConfig(String key) {
        return coreDiyConfigService.getBottomConfig(RequestUtils.siteId(), key);
    }

    @Override
    public void setBottomConfig(SetBottomConfigParam param) {
        coreDiyConfigService.setBottomConfig(RequestUtils.siteId(), param.getValue(), param.getKey());
    }

}
