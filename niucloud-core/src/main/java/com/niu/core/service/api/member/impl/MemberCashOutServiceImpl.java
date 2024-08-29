package com.niu.core.service.api.member.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.MemberCashOut;
import com.niu.core.entity.member.MemberCashOutAccount;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.mapper.member.MemberCashOutAccountMapper;
import com.niu.core.mapper.member.MemberCashOutMapper;
import com.niu.core.service.admin.member.vo.CashOutConfigVo;
import com.niu.core.service.api.member.IMemberCashOutService;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.*;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.sys.ICoreConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class MemberCashOutServiceImpl implements IMemberCashOutService {

    @Autowired
    private MemberCashOutMapper memberCashOutMapper;

    @Autowired
    private MemberCashOutAccountMapper memberCashOutAccountMapper;

    @Autowired
    private ICoreMemberConfigService coreMemberConfigService;

    @Override
    public PageResult<?> cashOutList(PageParam pageParam, MemberCashOutListParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MemberCashOut> queryWrapper = new QueryWrapper();
        queryWrapper.select("id,site_id,cash_out_no,member_id,account_type,transfer_type,transfer_realname,transfer_mobile,transfer_bank,transfer_account,transfer_status,transfer_time,apply_money,rate,service_money,money,audit_time,status,remark,create_time,refuse_reason");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        queryWrapper.like("status", param.getStatus());
        queryWrapper.like("create_time", param.getCreateTime());
        queryWrapper.orderByDesc("create_time");

        IPage<MemberCashOut> iPage = this.memberCashOutMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberCashOutListVo> memberCashOutListVoList = CollectUtils.convert(iPage.getRecords(), MemberCashOutListVo.class);
        if (memberCashOutListVoList != null) {
//            for (MemberCashOutListVo memberCashOutListVo : memberCashOutListVoList) {
//
//            }
        }
        return PageResult.build(page, limit).setData(memberCashOutListVoList);
    }

    @Override
    public MemberCashOutInfoVo cashOutInfo(MemberCashOutInfoParam param) {
        QueryWrapper<MemberCashOut> queryWrapper = new QueryWrapper();
        queryWrapper.select("id,site_id,cash_out_no,member_id,transfer_type,transfer_realname,transfer_mobile,transfer_bank,transfer_account,transfer_fail_reason,transfer_time,apply_money,rate,service_money,money,audit_time,status,remark,create_time,refuse_reason");
        queryWrapper.eq("id", param.getId());
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        MemberCashOut memberCashOut = memberCashOutMapper.selectOne(queryWrapper);
        MemberCashOutInfoVo memberCashOutInfoVo = new MemberCashOutInfoVo();
        BeanUtils.copyProperties(memberCashOut, memberCashOutInfoVo);
        return memberCashOutInfoVo;
    }

    @Override
    public CashOutConfigVo cashOutConfig(MemberCashOutConfigParam param) {
        return coreMemberConfigService.getCashOutConfig(param.siteId());
    }

    /**
     * 提现转账方式
     *
     * @param param
     * @return
     */
    @Override
    public Object transferMethod(MemberCashOutTransferMethodParam param) {
        return TransferTypeEnum.getMap();
    }

    /**
     * 会员提现
     *
     * @param param
     * @return
     */
    @Override
    public boolean cashOutApply(MemberCashOutApplyParam param) {
        //
        return true;
    }

    /**
     * 撤销提现申请
     *
     * @param param
     * @return
     */
    @Override
    public boolean cashOutCancel(MemberCashOutCancelParam param) {
        //
        return true;
    }

    @Override
    public PageResult accountList(PageParam pageParam, MemberCashOutAccountListParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<MemberCashOutAccount> queryWrapper = new QueryWrapper();
        queryWrapper.select("account_id,site_id,member_id,account_type,bank_name,realname,account_no");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        queryWrapper.orderByDesc(Arrays.asList("create_time"));
        IPage<MemberCashOutAccount> iPage = this.memberCashOutAccountMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberCashOutAccountVo> dataList = CollectUtils.convert(iPage.getRecords(), MemberCashOutAccountVo.class);
        return PageResult.build(page, limit).setData(dataList);
    }

    @Override
    public MemberCashOutAccountDetailsVo accountDetails(MemberCashOutAccountDetailsParam param) {
        QueryWrapper<MemberCashOutAccount> queryWrapper = new QueryWrapper();
        queryWrapper.select("account_id,site_id,member_id,account_type,bank_name,realname,account_no");
        queryWrapper.eq("account_id", param.getAccountId());
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        MemberCashOutAccount memberCashOutAccount = this.memberCashOutAccountMapper.selectOne(queryWrapper);
        MemberCashOutAccountDetailsVo memberCashOutAccountDetailsVo = new MemberCashOutAccountDetailsVo();
        BeanUtils.copyProperties(memberCashOutAccount, memberCashOutAccountDetailsVo);
        return memberCashOutAccountDetailsVo;
    }

    @Override
    public MemberCashOutFirstAccountDetailsVo accountDetailsOfFirst(MemberCashOutFirstAccountDetailsParam param) {
        QueryWrapper<MemberCashOutAccount> queryWrapper = new QueryWrapper();
        queryWrapper.select("account_id,site_id,member_id,account_type,bank_name,realname,account_no");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 1");
        MemberCashOutAccount memberCashOutAccount = this.memberCashOutAccountMapper.selectOne(queryWrapper);
        if (memberCashOutAccount == null) return null;
        MemberCashOutFirstAccountDetailsVo memberCashOutFirstAccountDetailsVo = new MemberCashOutFirstAccountDetailsVo();
        BeanUtils.copyProperties(memberCashOutAccount, memberCashOutFirstAccountDetailsVo);
        return memberCashOutFirstAccountDetailsVo;
    }

    @Override
    public Integer addAccount(MemberCashOutAccountAddParam param) {
        // 构建新的对象，并插入数据库
        MemberCashOutAccount newMemberCashOutAccount = new MemberCashOutAccount();
        newMemberCashOutAccount.setSiteId(param.siteId());
        newMemberCashOutAccount.setMemberId(param.memberId());
        newMemberCashOutAccount.setCreateTime(System.currentTimeMillis() / 1000);
        BeanUtils.copyProperties(param, newMemberCashOutAccount);
        this.memberCashOutAccountMapper.insert(newMemberCashOutAccount);
        // 返回当前对象的ID
        return newMemberCashOutAccount.getAccountId();
    }

    @Override
    public Integer editAccount(Integer accountId, MemberCashOutAccountEditParam param) {
        // 根据ID查找实体对象，对象存在即更新数据库
        MemberCashOutAccount oldMemberCashOutAccount = this.memberCashOutAccountMapper.selectOne(new QueryWrapper<MemberCashOutAccount>()
                .eq("account_id", accountId)
                .eq("member_id", param.memberId()));
        if (oldMemberCashOutAccount == null) {
            throw new RuntimeException("会员账户不存在");
        }
        MemberCashOutAccount newMemberCashOutAccount = new MemberCashOutAccount();
        BeanUtils.copyProperties(param, newMemberCashOutAccount);
        newMemberCashOutAccount.setAccountId(accountId);
        newMemberCashOutAccount.setUpdateTime(System.currentTimeMillis() / 1000);
        return this.memberCashOutAccountMapper.updateById(newMemberCashOutAccount);
    }

    @Override
    public Integer deleteAccount(MemberCashOutAccountDeleteParam param) {
        return this.memberCashOutAccountMapper.delete(new QueryWrapper<MemberCashOutAccount>()
                .eq("account_id", param.getAccountId())
                .eq("member_id", param.memberId()));
    }

}
