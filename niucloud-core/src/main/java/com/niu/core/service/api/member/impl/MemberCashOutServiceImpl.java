package com.niu.core.service.api.member.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.MemberCashOut;
import com.niu.core.entity.member.MemberCashOutAccount;
import com.niu.core.enums.member.MemberCashOutStatusEnum;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.mapper.member.MemberCashOutAccountMapper;
import com.niu.core.mapper.member.MemberCashOutMapper;
import com.niu.core.service.admin.member.param.CashOutTransferParam;
import com.niu.core.service.admin.member.vo.CashOutConfigVo;
import com.niu.core.service.api.member.IMemberCashOutService;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.*;
import com.niu.core.service.core.member.ICoreMemberCashOutService;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.member.param.MemberCashOutApplyParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private ICoreMemberCashOutService coreMemberCashOutService;

    @Override
    public PageResult<?> cashOutList(PageParam pageParam, MemberCashOutListParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MemberCashOut> queryWrapper = new QueryWrapper();
        queryWrapper.select("id,site_id,cash_out_no,member_id,account_type,transfer_type,transfer_realname,transfer_payment_code,transfer_mobile,transfer_bank,transfer_account,transfer_status,transfer_time,apply_money,rate,service_money,money,audit_time,status,remark,create_time,refuse_reason");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        if (ObjectUtil.isNotEmpty(param.getStatus())) queryWrapper.like("status", param.getStatus());
        if (ObjectUtil.isNotEmpty(param.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", param.getCreateTime());
        queryWrapper.orderByDesc("create_time");

        IPage<MemberCashOut> iPage = this.memberCashOutMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberCashOutListVo> memberCashOutListVoList = CollectUtils.convert(iPage.getRecords(), MemberCashOutListVo.class);
        if (memberCashOutListVoList != null) {
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(memberCashOutListVoList);
    }

    @Override
    public MemberCashOutInfoVo cashOutInfo(MemberCashOutInfoParam param) {
        MemberCashOutInfoVo vo = memberCashOutMapper.selectJoinOne(
            MemberCashOutInfoVo.class,
            new MPJQueryWrapper<MemberCashOut>()
                .select("mco.*,m.member_no,m.username,m.nickname,m.mobile,m.headimg,pt.transfer_voucher,pt.transfer_remark,pt.extra,pt.package_info")
                .eq("mco.id", param.getId())
                .eq("mco.member_id", param.memberId())
                .eq("mco.site_id", param.siteId())
                .setAlias("mco")
                .leftJoin("?_member m ON mco.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_pay_transfer pt ON mco.transfer_no = pt.transfer_no".replace("?_", GlobalConfig.tablePrefix))
        );

        if (vo != null) {
            MemberCashOutInfoVo.Transfer transfer = new MemberCashOutInfoVo.Transfer();
            transfer.setTransferNo(vo.getTransferNo());
            transfer.setTransferRemark(vo.getTransferRemark());
            transfer.setTransferVoucher(vo.getTransferVoucher());
            transfer.setExtra(vo.getExtra());
            transfer.setPackageInfo(vo.getPackageInfo());
            vo.setTransfer(transfer);
        }

        return vo;
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
    public Integer cashOutApply(MemberCashOutApplyParam param) {
        param.setMemberId(RequestUtils.memberId());
        param.setSiteId(RequestUtils.siteId());
        return coreMemberCashOutService.apply(param);
    }

    /**
     * 撤销提现申请
     *
     * @param param
     * @return
     */
    @Override
    public boolean cashOutCancel(MemberCashOutCancelParam param) {
        MemberCashOut cashOut = memberCashOutMapper.selectOne(new QueryWrapper<MemberCashOut>()
                .eq("member_id", param.memberId())
                .eq("site_id", param.siteId())
                .eq("id", param.getId()));
        if (ObjectUtil.isEmpty(cashOut)) throw new CommonException("提现申请不存在");

        coreMemberCashOutService.cancel(cashOut.getSiteId(), cashOut.getId());
        return true;
    }

    /**
     * 开始转账
     * @param param
     */
    @Override
    public Object transfer(CashOutTransferParam param) {
        MemberCashOut cashOut = memberCashOutMapper.selectOne(new QueryWrapper<MemberCashOut>()
            .eq("member_id", RequestUtils.memberId())
            .eq("site_id", RequestUtils.siteId())
            .eq("id", param.getId()));
        if (ObjectUtil.isEmpty(cashOut)) throw new CommonException("提现申请不存在");
        if (!cashOut.getStatus().equals(MemberCashOutStatusEnum.WAIT_TRANSFER.getStatus())) throw new CommonException("当前提现申请未处于待转账状态");

        param.setChannel(RequestUtils.channel());
        return coreMemberCashOutService.transfer(cashOut, param);
    }

    @Override
    public PageResult accountList(PageParam pageParam, MemberCashOutAccountListParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<MemberCashOutAccount> queryWrapper = new QueryWrapper();
        queryWrapper.select("account_id,site_id,member_id,account_type,bank_name,realname,account_no,transfer_payment_code");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        if (ObjectUtil.isNotEmpty(param.getAccountType())) queryWrapper.eq("account_type", param.getAccountType());
        queryWrapper.orderByDesc(Arrays.asList("create_time"));
        IPage<MemberCashOutAccount> iPage = this.memberCashOutAccountMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberCashOutAccountVo> dataList = CollectUtils.convert(iPage.getRecords(), MemberCashOutAccountVo.class);
        return PageResult.build(page, limit, iPage.getTotal()).setData(dataList);
    }

    @Override
    public MemberCashOutAccountDetailsVo accountDetails(MemberCashOutAccountDetailsParam param) {
        QueryWrapper<MemberCashOutAccount> queryWrapper = new QueryWrapper();
        queryWrapper.select("account_id,site_id,member_id,account_type,bank_name,realname,account_no,transfer_payment_code");
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
        queryWrapper.select("account_id,site_id,member_id,account_type,bank_name,realname,account_no,transfer_payment_code");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        if (ObjectUtil.isNotEmpty(param.getAccountType())) queryWrapper.eq("account_type", param.getAccountType());
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
