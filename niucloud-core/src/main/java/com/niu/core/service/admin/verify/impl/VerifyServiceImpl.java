package com.niu.core.service.admin.verify.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.verify.Verify;
import com.niu.core.event.sys.VerifyInfoEventDefiner;
import com.niu.core.mapper.verify.VerifyMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.verify.param.VerifySearchParam;
import com.niu.core.service.admin.verify.IVerifyService;
import com.niu.core.service.admin.verify.vo.VerifyInfoVo;
import com.niu.core.service.admin.verify.vo.VerifyListVo;
import com.niu.core.service.admin.verify.vo.VerifyVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.verify.ICoreVerifyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 核销记录实现
 */
@Service
public class VerifyServiceImpl implements IVerifyService {
        
    @Resource
    VerifyMapper verifyMapper;

    @Resource
    ICoreVerifyService coreVerifyService;

    /**
     * 核销记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<VerifyListVo>
     */
    @Override
    public PageResult<VerifyListVo> list(PageParam pageParam, VerifySearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<Verify> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("v").leftJoin("?_member m ON v.verifier_member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("v.*,m.member_id,m.member_no,m.username,m.nickname,m.mobile,m.headimg");
        queryWrapper.eq("v.site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("id");

        if (ObjectUtil.isNotEmpty(searchParam.getCode())) queryWrapper.eq("code", searchParam.getCode());
        if (ObjectUtil.isNotEmpty(searchParam.getType())) queryWrapper.eq("type", searchParam.getType());
        if (ObjectUtil.isNotEmpty(searchParam.getVerifierMemberId())) queryWrapper.eq("verifier_member_id", searchParam.getVerifierMemberId());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "v.create_time", searchParam.getCreateTime());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderId())) {
            queryWrapper.apply("JSON_EXTRACT(data, '$.order_id') = {0}", searchParam.getOrderId().toString());
        }

        IPage<VerifyVo> iPage = verifyMapper.selectJoinPage(new Page<>(page, limit), VerifyVo.class, queryWrapper);
        List<VerifyListVo> list = new LinkedList<>();
        for (VerifyVo item : iPage.getRecords()) {
            VerifyListVo vo = new VerifyListVo();
            BeanUtils.copyProperties(item, vo);

            MemberBriefInfoVo memberInfoVo = new MemberBriefInfoVo();
            BeanUtils.copyProperties(item, memberInfoVo);
            vo.setMember(memberInfoVo);

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 核销记录详情
     * @return VerifyInfoVo
     */
    @Override
    public VerifyInfoVo detail(String code) {
        MPJQueryWrapper<Verify> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("v").leftJoin("?_member m ON v.verifier_member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("v.*,m.member_no,m.username,m.nickname,m.mobile,m.headimg");
        queryWrapper.eq("v.site_id", RequestUtils.siteId());
        queryWrapper.eq("v.code", code);

        VerifyVo model = verifyMapper.selectJoinOne(VerifyVo.class, queryWrapper);
        Assert.notNull(model, "数据不存在");

        VerifyInfoVo vo = new VerifyInfoVo();
        BeanUtils.copyProperties(model, vo);

        MemberBriefInfoVo memberInfoVo = new MemberBriefInfoVo();
        BeanUtils.copyProperties(model, memberInfoVo);
        vo.setMember(memberInfoVo);

        VerifyInfoEventDefiner.VerifyInfoEvent event = new VerifyInfoEventDefiner.VerifyInfoEvent();
        event.setData(vo);
        List<VerifyInfoEventDefiner.VerifyInfoEventResult> list = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if (!CollectionUtils.isEmpty(list)){
            for (VerifyInfoEventDefiner.VerifyInfoEventResult verifyInfoEventResult : list) {
                if (verifyInfoEventResult != null && verifyInfoEventResult.getDataMap() != null && !verifyInfoEventResult.getDataMap().isEmpty()){
                    vo.setVerifyInfo(verifyInfoEventResult.getDataMap());
                    break;
                }
            }
        }
        return vo;
    }

    @Override
    public Map<String, Object> getInfoByCode(String code) {
        return coreVerifyService.adminGetInfoByCode(RequestUtils.siteId(), code);
    }

    /**
     * 核销
     * @param code
     */
    @Override
    public void verify(String code) {
        coreVerifyService.adminVerify(RequestUtils.siteId(), code);
    }
}
