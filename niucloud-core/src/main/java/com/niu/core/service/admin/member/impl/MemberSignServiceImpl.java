package com.niu.core.service.admin.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.member.MemberSign;
import com.niu.core.mapper.member.MemberSignMapper;
import com.niu.core.service.admin.member.param.MemberSignSearchParam;
import com.niu.core.service.admin.member.IMemberSignService;
import com.niu.core.service.admin.member.param.SignConfigParam;
import com.niu.core.service.admin.member.vo.*;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.sys.ICoreConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 会员签到实现
 */
@Service
public class MemberSignServiceImpl implements IMemberSignService {
        
    @Resource
    MemberSignMapper memberSignMapper;

    @Resource
    ICoreConfigService iCoreConfigService;

    @Resource
    ICoreMemberService coreMemberService;

    /**
     * 会员签到列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberSignListVo>
     */
    @Override
    public PageResult<MemberSignListVo> pages(PageParam pageParam, MemberSignSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<MemberSign> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ms").innerJoin("?_member m ON ms.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("ms.*,m.member_no,m.username,m.nickname,m.mobile,m.headimg");
        queryWrapper.eq("ms.site_id", siteId);
        queryWrapper.orderByDesc("ms.sign_id");

        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) queryWrapper.like("m.member_no|m.username|m.nickname|m.mobile", searchParam.getKeywords());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "ms.create_time", searchParam.getCreateTime());

        IPage<MemberSign> iPage = memberSignMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberSignListVo> list = new LinkedList<>();
        for (MemberSign item : iPage.getRecords()) {
            MemberSignListVo vo = new MemberSignListVo();
            BeanUtils.copyProperties(item, vo);

            if (!item.getDayAward().isEmpty()) vo.setDayAward(coreMemberService.getGiftContent(item.getSiteId(), JSONUtil.parseObj(item.getDayAward()), "admin"));
            if (!item.getContinueAward().isEmpty()) vo.setContinueAward(coreMemberService.getGiftContent(item.getSiteId(), JSONUtil.parseObj(item.getContinueAward()), "admin"));

            MemberBriefInfoVo memberInfoVo = new MemberBriefInfoVo();
            BeanUtils.copyProperties(item, memberInfoVo);

            vo.setMember(memberInfoVo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 获取签到设置
     */
    @Override
    public SignConfigVo getSignConfig() {
        JSONObject config = iCoreConfigService.getConfigValue(RequestUtils.siteId(), "SIGN_CONFIG");
        return JSONUtil.toBean(config, SignConfigVo.class);
    }

    /**
     * 设置签到设置
     * @param configParam
     */
    @Override
    public void setSignConfig(SignConfigParam configParam) {
        iCoreConfigService.setConfig(RequestUtils.siteId(), "SIGN_CONFIG", JSONUtil.parseObj(configParam));
    }
}
