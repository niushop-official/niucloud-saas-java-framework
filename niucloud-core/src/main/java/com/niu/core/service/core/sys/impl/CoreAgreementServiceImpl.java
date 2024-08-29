package com.niu.core.service.core.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.sys.SysAgreement;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.mapper.sys.SysAgreementMapper;
import com.niu.core.service.core.sys.ICoreAgreementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreAgreementServiceImpl implements ICoreAgreementService {

    @Resource
    SysAgreementMapper sysAgreementMapper;
    @Override
    public SysAgreement getAgreement(Integer siteId, String key) {
        SysAgreement model=sysAgreementMapper.selectOne(new QueryWrapper<SysAgreement>().eq("agreement_key", key).eq("site_id", siteId).last(" limit 1"));
        if(ObjectUtil.isNull(model))
        {
            SysAgreement defaultModel = new SysAgreement();
            defaultModel.setSiteId(siteId);
            defaultModel.setAgreementKey(key);
            defaultModel.setContent("");
            defaultModel.setTitle("");
            return defaultModel;
        }
        return model;
    }

    @Override
    public void setAgreement(Integer siteId, String key, String title, String content) {
        SysAgreement model=sysAgreementMapper.selectOne(new QueryWrapper<SysAgreement>().eq("agreement_key", key).eq("site_id", siteId).last(" limit 1"));
        if(ObjectUtil.isNull(model)){
            SysAgreement addModel = new SysAgreement();
            addModel.setSiteId(siteId);
            addModel.setAgreementKey(key);
            addModel.setTitle(title);
            addModel.setContent(content);
            addModel.setCreateTime(System.currentTimeMillis() / 1000);
            sysAgreementMapper.insert(addModel);
        }else{
            model.setUpdateTime(System.currentTimeMillis() / 1000);
            model.setTitle(title);
            model.setContent(content);
            sysAgreementMapper.updateById(model);

        }
    }
}
