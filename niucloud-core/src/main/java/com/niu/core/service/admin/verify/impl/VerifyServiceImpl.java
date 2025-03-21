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
import com.niu.core.mapper.verify.VerifyMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.verify.param.VerifySearchParam;
import com.niu.core.service.admin.verify.IVerifyService;
import com.niu.core.service.admin.verify.vo.VerifyInfoVo;
import com.niu.core.service.admin.verify.vo.VerifyListVo;
import com.niu.core.service.admin.verify.vo.VerifyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 核销记录实现
 */
@Service
public class VerifyServiceImpl implements IVerifyService {
        
    @Resource
    VerifyMapper verifyMapper;

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
        queryWrapper.setAlias("v").innerJoin("?_member m ON v.verifier_member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("v.*,m.member_no,m.username,m.nickname,m.mobile,m.headimg");
        queryWrapper.eq("v.site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("id");

        if (ObjectUtil.isNotEmpty(searchParam.getCode())) queryWrapper.eq("code", searchParam.getCode());
        if (ObjectUtil.isNotEmpty(searchParam.getType())) queryWrapper.eq("type", searchParam.getType());
        if (ObjectUtil.isNotEmpty(searchParam.getVerifierMemberId())) queryWrapper.eq("verifier_member_id", searchParam.getVerifierMemberId());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "v.create_time", searchParam.getCreateTime());

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
        queryWrapper.setAlias("v").innerJoin("?_member m ON v.verifier_member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
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

        return vo;
    }
}
