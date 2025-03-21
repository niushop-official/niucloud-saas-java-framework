package com.niu.core.service.admin.diy_form.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.diy_form.IDiyFormConfigService;
import com.niu.core.service.core.diy_form.ICoreDiyFormConfigService;
import com.niu.core.service.core.diy_form.param.DiyFormSubmitConfigParam;
import com.niu.core.service.core.diy_form.param.DiyFormWriteConfigParam;
import com.niu.core.service.core.diy_form.vo.DiyFormSubmitConfigInfoVo;
import com.niu.core.service.core.diy_form.vo.DiyFormWriteConfigInfoVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 万能表单填写配置实现
 */
@Service
public class DiyFormConfigServiceImpl implements IDiyFormConfigService {

    @Resource
    private ICoreDiyFormConfigService coreDiyFormConfigService;

    @Override
    public DiyFormWriteConfigInfoVo getWriteConfig(Integer formId) {
        return coreDiyFormConfigService.getWriteConfig(RequestUtils.siteId(), formId);
    }

    @Override
    public void editWriteConfig(DiyFormWriteConfigParam editParam) {
        editParam.setSiteId(RequestUtils.siteId());
        coreDiyFormConfigService.editWriteConfig(editParam);
    }

    @Override
    public DiyFormSubmitConfigInfoVo getSubmitConfig(Integer formId) {
        return coreDiyFormConfigService.getSubmitConfig(RequestUtils.siteId(), formId);
    }

    @Override
    public void editSubmitConfig(DiyFormSubmitConfigParam editParam) {
        editParam.setSiteId(RequestUtils.siteId());
        coreDiyFormConfigService.editSubmitConfig(editParam);
    }
}
