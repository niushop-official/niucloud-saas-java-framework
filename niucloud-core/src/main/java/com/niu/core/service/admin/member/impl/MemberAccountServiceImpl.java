package com.niu.core.service.admin.member.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberAccountLog;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.mapper.member.MemberAccountLogMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.IMemberAccountService;
import com.niu.core.service.admin.member.param.AdjustAccountParam;
import com.niu.core.service.admin.member.param.MemberAccountLogSearchParam;
import com.niu.core.service.admin.member.vo.*;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;


/**
 * 会员账单实现
 */
@Service
public class MemberAccountServiceImpl implements IMemberAccountService {

    @Resource
    MemberAccountLogMapper memberAccountLogMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    /**
     * 会员账单列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberAccountLogListVo>
     */
    @Override
    public PageResult<MemberAccountLogListVo> list(PageParam pageParam, MemberAccountLogSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MemberAccountLog> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("mal").innerJoin("?_member m ON mal.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("mal.*,m.member_no,m.username,m.nickname,m.mobile,m.headimg");
        queryWrapper.eq("mal.site_id", siteId);
        queryWrapper.eq("mal.account_type", searchParam.getAccountType());
        queryWrapper.orderByDesc("mal.id");

        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.and(wrapper -> {
                wrapper.like("m.member_no", searchParam.getKeywords())
                    .or()
                    .like("m.username", searchParam.getKeywords())
                    .or()
                    .like("m.nickname", searchParam.getKeywords())
                    .or()
                    .like("m.mobile", searchParam.getKeywords());
            });
        }
        if (ObjectUtil.defaultIfNull(searchParam.getMemberId(), 0) > 0)
            queryWrapper.eq("mal.member_id", searchParam.getMemberId());
        if (ObjectUtil.isNotEmpty(searchParam.getFromType()))
            queryWrapper.eq("mal.from_type", searchParam.getFromType());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime()))
            QueryMapperUtils.buildByTime(queryWrapper, "mal.create_time", searchParam.getCreateTime());

        IPage<MemberAccountLogVo> iPage = memberAccountLogMapper.selectJoinPage(new Page<>(page, limit), MemberAccountLogVo.class, queryWrapper);
        List<MemberAccountLogListVo> list = new LinkedList<>();
        for (MemberAccountLogVo item : iPage.getRecords()) {
            MemberAccountLogListVo vo = new MemberAccountLogListVo();
            BeanUtils.copyProperties(item, vo);

            MemberBriefInfoVo memberInfoVo = new MemberBriefInfoVo();
            BeanUtils.copyProperties(item, memberInfoVo);

            vo.setMember(memberInfoVo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 获取会员账户信息
     *
     * @param memberId
     * @return
     */
    @Override
    public MemberAccountVo getMemberAccountInfo(Integer memberId) {
        Integer siteId = RequestUtils.siteId();

        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .eq("member_id", memberId)
                .eq("site_id", siteId));
        Assert.notNull(member, "会员不存在");

        MemberAccountVo memberAccountVo = new MemberAccountVo();
        BeanUtils.copyProperties(member, memberAccountVo);

        return memberAccountVo;
    }

    /**
     * 查询佣金统计
     *
     * @param searchParam
     * @return
     */
    @Override
    public SumCommissionVo sumCommission(MemberAccountLogSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();
        SumCommissionVo vo = new SumCommissionVo();
        BigDecimal zero = (new BigDecimal(0));

        if (ObjectUtil.isNotEmpty(searchParam.getMemberId()) && searchParam.getMemberId() > 0) {
            MemberAccountVo memberAccountInfo = this.getMemberAccountInfo(searchParam.getMemberId());
            vo.setCommission(memberAccountInfo.getCommission());
            vo.setCommissionCashOuting(memberAccountInfo.getCommissionCashOuting());
            vo.setTotalCommission(memberAccountInfo.getCommissionGet());
            MemberAccountLog memberAccountLog = memberAccountLogMapper.selectOne(new QueryWrapper<MemberAccountLog>()
                    .select("SUM(account_data) AS account_sum")
                    .eq("member_id", searchParam.getMemberId())
                    .eq("site_id", siteId)
                    .eq("account_type", AccountTypeEnum.COMMISSION.getType())
                    .eq("from_type", "cash_out"));
            vo.setWithdrawnCommission(memberAccountLog == null ? zero : memberAccountLog.getAccountSum());
        } else {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                    .select("SUM(commission_get) AS commission_get,SUM(commission) AS commission, SUM(commission_cash_outing) AS commission_cash_outing")
                    .eq("site_id", siteId));

            vo.setCommission(member == null ? zero : member.getCommission());
            vo.setCommissionCashOuting(member == null ? zero : member.getCommissionCashOuting());
            vo.setTotalCommission(member == null ? zero : member.getCommissionGet());
            MemberAccountLog memberAccountLog = memberAccountLogMapper.selectOne(new QueryWrapper<MemberAccountLog>()
                    .select("SUM(account_data) AS account_sum")
                    .eq("site_id", siteId)
                    .eq("account_type", AccountTypeEnum.COMMISSION.getType())
                    .eq("from_type", "cash_out"));
            vo.setWithdrawnCommission(memberAccountLog == null ? zero : memberAccountLog.getAccountSum());
        }
        return vo;
    }

    /**
     * 查询余额统计
     *
     * @param searchParam
     * @return
     */
    @Override
    public SumBalanceVo sumBalance(MemberAccountLogSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();
        SumBalanceVo vo = new SumBalanceVo();

        if (ObjectUtil.isNotEmpty(searchParam.getMemberId()) && searchParam.getMemberId() > 0) {
            MemberAccountVo memberAccountInfo = this.getMemberAccountInfo(searchParam.getMemberId());

            vo.setBalance(memberAccountInfo == null ? new BigDecimal(0) : memberAccountInfo.getBalance());
            vo.setMoney(memberAccountInfo == null ? new BigDecimal(0) : memberAccountInfo.getMoney());
        } else {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                    .select("SUM(balance) AS balance,SUM(money) AS money")
                    .eq("site_id", siteId));

            vo.setBalance(member == null ? new BigDecimal(0) : member.getBalance());
            vo.setMoney(member == null ? new BigDecimal(0) : member.getMoney());
        }

        return vo;
    }

    /**
     * 查询积分统计
     *
     * @param searchParam
     * @return
     */
    @Override
    public SumPointVo sumPoint(MemberAccountLogSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();
        SumPointVo vo = new SumPointVo();

        if (ObjectUtil.isNotEmpty(searchParam.getMemberId()) && searchParam.getMemberId() > 0) {
            MemberAccountVo memberAccountInfo = this.getMemberAccountInfo(searchParam.getMemberId());
            MemberAccountLog memberAccountLog = memberAccountLogMapper.selectOne(new QueryWrapper<MemberAccountLog>()
                    .select("SUM(account_data) AS account_sum")
                    .eq("site_id", siteId)
                    .eq("member_id", searchParam.getMemberId())
                    .eq("account_type", AccountTypeEnum.POINT.getType())
                    .lt("account_data", 0));
            vo.setPointGet(memberAccountInfo.getPointGet());
            vo.setPointUse(memberAccountLog == null ? 0 : memberAccountLog.getAccountSum().abs().intValue());
        } else {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                    .select("SUM(point_get) AS point_get")
                    .eq("site_id", siteId));
            vo.setPointGet(member == null ? 0 : member.getPointGet());

            MemberAccountLog memberAccountLog = memberAccountLogMapper.selectOne(new QueryWrapper<MemberAccountLog>()
                    .select("SUM(account_data) AS account_sum")
                    .eq("site_id", siteId)
                    .eq("account_type", AccountTypeEnum.POINT.getType())
                    .lt("account_data", 0));
            vo.setPointUse(memberAccountLog == null ? 0 : memberAccountLog.getAccountSum().abs().intValue());
        }

        return vo;
    }

    /**
     * 调整积分
     *
     * @param param
     */
    public void adjustPoint(AdjustAccountParam param) {
        coreMemberAccountService.addLog(RequestUtils.siteId(), param.getMemberId(), AccountTypeEnum.POINT.getType(), param.getAccountData(), "adjust", param.getMemo(), "");
    }

    /**
     * 调整余额
     *
     * @param param
     */
    public void adjustBalance(AdjustAccountParam param) {
        coreMemberAccountService.addLog(RequestUtils.siteId(), param.getMemberId(), AccountTypeEnum.BALANCE.getType(), param.getAccountData(), "adjust", param.getMemo(), "");
    }
}
