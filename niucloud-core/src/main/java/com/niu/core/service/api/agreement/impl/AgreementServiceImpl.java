package com.niu.core.service.api.agreement.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.sys.SysAgreement;
import com.niu.core.enums.sys.AgreementEnum;
import com.niu.core.mapper.sys.SysAgreementMapper;
import com.niu.core.service.api.agreement.IAgreementService;
import com.niu.core.service.api.agreement.param.AgreementInfoParam;
import com.niu.core.service.api.agreement.vo.AgreementInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AgreementServiceImpl implements IAgreementService {

    @Autowired
    private SysAgreementMapper sysAgreementMapper;

    @Override
    public AgreementInfoVo info(AgreementInfoParam param) {
        QueryWrapper<SysAgreement> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("site_id, agreement_key, title, content, create_time, update_time");
        queryWrapper.eq("agreement_key", param.getKey());
        queryWrapper.eq("site_id", param.siteId());
        SysAgreement sysAgreement = sysAgreementMapper.selectOne(queryWrapper);
        if (sysAgreement == null) {
            return null;
        }
        AgreementInfoVo agreementInfoVo = new AgreementInfoVo();
        BeanUtils.copyProperties(sysAgreement, agreementInfoVo);
        return agreementInfoVo;
    }

}
