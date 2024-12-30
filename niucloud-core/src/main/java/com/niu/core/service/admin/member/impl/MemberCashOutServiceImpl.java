package com.niu.core.service.admin.member.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberCashOut;
import com.niu.core.enums.member.MemberCashOutStatusEnum;
import com.niu.core.mapper.member.MemberCashOutMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.param.CashOutTransferParam;
import com.niu.core.service.admin.member.param.MemberCashOutAuditParam;
import com.niu.core.service.admin.member.param.MemberCashOutSearchParam;
import com.niu.core.service.admin.member.IMemberCashOutService;
import com.niu.core.service.admin.member.vo.*;
import com.niu.core.service.core.member.ICoreMemberCashOutService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;


/**
 * 会员提现实现
 */
@Service
public class MemberCashOutServiceImpl implements IMemberCashOutService {
        
    @Resource
    MemberCashOutMapper memberCashOutMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreMemberCashOutService iCoreMemberCashOutService;

    /**
     * 会员提现列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberCashOutListVo>
     */
    @Override
    public PageResult<MemberCashOutListVo> pages(PageParam pageParam, MemberCashOutSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<MemberCashOut> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("mco").innerJoin("?_member m ON mco.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("mco.*,m.member_no,m.username,m.nickname,m.mobile,m.headimg");
        queryWrapper.eq("mco.site_id", siteId);
        queryWrapper.orderByDesc("mco.id");

        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.and(i ->
                    i.like("m.member_no", searchParam.getKeywords())
                            .or().like("m.username", searchParam.getKeywords())
                            .or().like("m.nickname", searchParam.getKeywords())
                            .or().like("m.mobile", searchParam.getKeywords())
            );
        }
        if (ObjectUtil.isNotEmpty(searchParam.getMemberId()) && searchParam.getMemberId() > 0) queryWrapper.eq("mco.member_id", searchParam.getMemberId());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("mco.status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getCashOutNo())) queryWrapper.like("mco.cash_out_no", searchParam.getCashOutNo());
        if (ObjectUtil.isNotEmpty(searchParam.getTransferType())) queryWrapper.like("mco.transfer_type", searchParam.getTransferType());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "mco.create_time", searchParam.getCreateTime());
        if (ObjectUtil.isNotEmpty(searchParam.getTransferTime())) QueryMapperUtils.buildByTime(queryWrapper, "mco.transfer_time", searchParam.getTransferTime());

        IPage<MemberCashOutVo> iPage = memberCashOutMapper.selectJoinPage(new Page<>(page, limit), MemberCashOutVo.class, queryWrapper);
        List<MemberCashOutListVo> list = new LinkedList<>();
        for (MemberCashOutVo item : iPage.getRecords()) {
            MemberCashOutListVo vo = new MemberCashOutListVo();
            BeanUtils.copyProperties(item, vo);

            MemberBriefInfoVo memberInfoVo = new MemberBriefInfoVo();
            BeanUtils.copyProperties(item, memberInfoVo);
            vo.setMember(memberInfoVo);

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 会员提现详情
     * @param id 主键
     * @return MemberCashOutInfoVo
     */
    @Override
    public MemberCashOutInfoVo info(Integer id) {
        Integer siteId = RequestUtils.siteId();

        MemberCashOut model = memberCashOutMapper.selectOne(
            new QueryWrapper<MemberCashOut>()
                .eq("id", id)
                .eq("site_id", siteId)
                .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        MemberCashOutInfoVo vo = new MemberCashOutInfoVo();
        BeanUtils.copyProperties(model, vo);

        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .eq("member_id", model.getMemberId())
                .eq("site_id", siteId));

        MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
        BeanUtils.copyProperties(member, memberVo);
        vo.setMemberInfo(memberVo);

        return vo;
    }

    /**
     * 统计
     * @return
     */
    public CashOutStatVo stat() {
        Integer siteId = RequestUtils.siteId();
        CashOutStatVo vo = new CashOutStatVo();

        MemberCashOut transfered = memberCashOutMapper.selectOne(
                new QueryWrapper<MemberCashOut>()
                        .eq("status", MemberCashOutStatusEnum.TRANSFERED.getStatus())
                        .eq("site_id", siteId)
                        .select("SUM(apply_money) AS apply_money")
                        .last("limit 1"));

        MemberCashOut allMoney = memberCashOutMapper.selectOne(
                new QueryWrapper<MemberCashOut>()
                        .ge("status", 0)
                        .eq("site_id", siteId)
                        .select("SUM(apply_money) AS apply_money")
                        .last("limit 1"));

        BigDecimal zero = new BigDecimal(0);
        vo.setTransfered(transfered == null ? zero : transfered.getApplyMoney());
        if (allMoney != null) {
            vo.setCashOuting(allMoney.getApplyMoney().subtract(vo.getTransfered()));
        } else {
            vo.setCashOuting(zero);
        }
        return vo;
    }

    /**
     * 提现审核
     * @param param
     */
    public void audit(MemberCashOutAuditParam param) {
        iCoreMemberCashOutService.audit(RequestUtils.siteId(), param.getId(), param.getAction(), param);
    }

    /**
     * 转账
     * @param param
     */
    @Override
    public void transfer(CashOutTransferParam param) {
        Integer siteId = RequestUtils.siteId();

        MemberCashOut model = memberCashOutMapper.selectOne(
                new QueryWrapper<MemberCashOut>()
                        .eq("id", param.getId())
                        .eq("site_id", siteId)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        iCoreMemberCashOutService.transfer(model, param);
    }
}
