package com.niu.core.service.admin.sys.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.sys.SysAgreement;
import com.niu.core.enums.sys.AgreementEnum;
import com.niu.core.mapper.sys.SysAgreementMapper;
import com.niu.core.service.admin.sys.param.SysAgreementParam;
import com.niu.core.service.admin.sys.param.SysAgreementSearchParam;
import com.niu.core.service.admin.sys.ISysAgreementService;
import com.niu.core.service.admin.sys.vo.SysAgreementInfoVo;
import com.niu.core.service.admin.sys.vo.SysAgreementListVo;
import com.niu.core.service.core.sys.ICoreAgreementService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 协议实现
 */
@Service
public class SysAgreementServiceImpl implements ISysAgreementService {

    @Resource
    ICoreAgreementService coreAgreementService;

    /**
     * 协议列表
     *
     * @return PageResult<SysAgreementListVo>
     */
    @Override
    public List<SysAgreementListVo> list() {
        JSONObject typeJson = AgreementEnum.getType();
        List<SysAgreementListVo> list = new ArrayList<>();

        for (Map.Entry<String, Object> map : typeJson.entrySet()) {
            SysAgreementListVo vo = new SysAgreementListVo();
            SysAgreement sysAgreement = coreAgreementService.getAgreement(RequestUtils.siteId(), map.getKey());
            BeanUtils.copyProperties(sysAgreement, vo);
            list.add(vo);
        }

        return list;
    }

    @Override
    public SysAgreementInfoVo getAgreement(String key) {
        SysAgreement sysAgreement = coreAgreementService.getAgreement(RequestUtils.siteId(), key);
        SysAgreementInfoVo vo = new SysAgreementInfoVo();
        BeanUtils.copyProperties(sysAgreement, vo);
        return vo;
    }

    @Override
    public void setAgreement(String key, String title, String content) {
        coreAgreementService.setAgreement(RequestUtils.siteId(), key, title, content);
    }

}
