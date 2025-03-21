package com.niu.core.service.core.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberCashOut;
import com.niu.core.entity.member.MemberCashOutAccount;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.member.MemberCashOutStatusEnum;
import com.niu.core.enums.pay.PayMainType;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.mapper.member.MemberCashOutAccountMapper;
import com.niu.core.mapper.member.MemberCashOutMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.param.CashOutTransferParam;
import com.niu.core.service.admin.member.param.MemberCashOutAuditParam;
import com.niu.core.service.admin.member.vo.CashOutConfigVo;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.member.ICoreMemberCashOutService;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.member.param.MemberCashOutApplyParam;
import com.niu.core.service.core.pay.ICoreTransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class CoreMemberCashOutServiceImpl implements ICoreMemberCashOutService {

    @Resource
    MemberCashOutMapper memberCashOutMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreMemberConfigService coreMemberConfigService;

    @Resource
    MemberCashOutAccountMapper memberCashOutAccountMapper;

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    @Resource
    ICoreTransferService coreTransferService;

    /**
     * 申请提现
     * @param param
     */
    @Override
    public void apply(MemberCashOutApplyParam param) {
        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .eq("member_id", param.getMemberId())
                .eq("site_id", param.getSiteId()));
        if (ObjectUtil.isEmpty(member)) throw new CommonException("会员不存在");

        CashOutConfigVo config = coreMemberConfigService.getCashOutConfig(param.getSiteId());
        if (config.getIsOpen().equals(0)) throw new CommonException("会员提现业务未开启");

        BigDecimal applyMoney = param.getApplyMoney();
        if (applyMoney.compareTo(BigDecimal.ZERO) <= 0) throw new CommonException("提现金额必须大于0");
        if (param.getAccountType() == AccountTypeEnum.MONEY.getType() && applyMoney.compareTo(member.getMoney()) > 0) throw new CommonException("提现金额不能大于账户余额");
        if (param.getAccountType() == AccountTypeEnum.COMMISSION.getType() && applyMoney.compareTo(member.getCommission()) > 0) throw new CommonException("提现金额不能大于账户可提现金额");
        if (applyMoney.compareTo(config.getMin()) <= 0) throw new CommonException("提现金额不能小于最低提现金额");
        if (!config.getTransferType().contains(param.getTransferType())) throw new CommonException("当前会员提现方式未启用");

        MemberCashOutAccount cashoutAccount = null;
        if (!param.getTransferType().equals(TransferTypeEnum.WECHATPAY.getKey())) {
            cashoutAccount = memberCashOutAccountMapper.selectOne(new QueryWrapper<MemberCashOutAccount>()
                    .eq("member_id", param.getMemberId())
                    .eq("site_id", param.getSiteId())
                    .eq("account_id", param.getAccountId()));
            if (ObjectUtil.isEmpty(cashoutAccount)) throw new CommonException("提现账户不存在");
        }

        BigDecimal serviceMoney = applyMoney.multiply(config.getRate()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

        MemberCashOut model = new MemberCashOut();
        model.setMemberId(param.getMemberId());
        model.setSiteId(param.getSiteId());
        model.setCashOutNo(CommonUtils.createNo());
        model.setAccountType(param.getAccountType());
        model.setApplyMoney(applyMoney);
        model.setServiceMoney(serviceMoney);
        model.setMoney(applyMoney.subtract(serviceMoney));
        model.setTransferType(param.getTransferType());
        model.setTransferRealname(cashoutAccount == null ? "" : cashoutAccount.getRealname());
        model.setTransferMobile(member.getMobile());
        model.setTransferBank(cashoutAccount == null ? "" : cashoutAccount.getBankName());
        model.setTransferAccount(cashoutAccount == null ? "" : cashoutAccount.getAccountNo());
        model.setRate(config.getRate());

        this.addCashOutRecord(model, member, config);
    }

    @Transactional
    private void addCashOutRecord(MemberCashOut model, Member member, CashOutConfigVo config) {
        model.setStatus(MemberCashOutStatusEnum.WAIT_AUDIT.getStatus());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        memberCashOutMapper.insert(model);

        // 扣除对应账户金额
        coreMemberAccountService.addLog(model.getSiteId(), model.getMemberId(), model.getAccountType(), model.getApplyMoney().negate().doubleValue(), "cash_out", "会员申请提现", model.getId().toString());

        // 累加账户提现中金额
        Member updateMember = new Member();
        updateMember.setMemberId(member.getMemberId());
        if (model.getAccountType().equals(AccountTypeEnum.MONEY.getType())) {
            updateMember.setMoneyCashOuting(member.getMoneyCashOuting().add(model.getApplyMoney()));
        }
        if (model.getAccountType().equals(AccountTypeEnum.COMMISSION.getType())) {
            updateMember.setCommissionCashOuting(member.getCommissionCashOuting().add(model.getApplyMoney()));
        }
        memberMapper.updateById(updateMember);

        if (config.getIsAutoVerify().equals("1")) {
            // 自动审核通过
            this.agree(model.getSiteId(), model, null);
        }
    }

    /**
     * 提现审核
     * @param siteId
     * @param id
     * @param action
     * @param param
     */
    public void audit(Integer siteId, Integer id, String action, MemberCashOutAuditParam param) {
        MemberCashOut cashOut = memberCashOutMapper.selectOne(
                new QueryWrapper<MemberCashOut>()
                        .eq("id", id)
                        .eq("site_id", siteId)
                        .last("limit 1"));

        Assert.notNull(cashOut, "提现记录不存在");

        if (!cashOut.getStatus().equals(MemberCashOutStatusEnum.WAIT_AUDIT.getStatus())) {
            throw new CommonException("当前提现申请未处于待审核状态");
        }

        switch (action) {
            case "agree":
                this.agree(siteId, cashOut, param);
                break;
            case "refuse":
                this.refuse(siteId, cashOut, param);
                break;
        }
    }

    /**
     * 同意
     * @param siteId
     * @param cashOut
     * @param param
     */
    public void agree(Integer siteId, MemberCashOut cashOut, MemberCashOutAuditParam param) {
        cashOut.setAuditTime(System.currentTimeMillis() / 1000);
        cashOut.setStatus(MemberCashOutStatusEnum.WAIT_TRANSFER.getStatus());
        memberCashOutMapper.updateById(cashOut);

        CashOutConfigVo config = coreMemberConfigService.getCashOutConfig(cashOut.getSiteId());
        if (config.getIsAutoTransfer().equals("1")) {
            this.transfer(cashOut, new CashOutTransferParam());
        }
    }

    /**
     * 转账
     *
     * @param cashOut
     */
    public void transfer(MemberCashOut cashOut, CashOutTransferParam param) {
        Assert.notNull(cashOut, "提现记录不存在");
        if (!cashOut.getStatus().equals(MemberCashOutStatusEnum.WAIT_TRANSFER.getStatus())) throw new CommonException("当前提现申请未处于待转账状态");

        if (ObjectUtil.isEmpty(cashOut.getTransferNo())) {
            PayTransfer transfer = new PayTransfer();
            transfer.setSiteId(cashOut.getSiteId());
            transfer.setMainType(PayMainType.MEMBER.getType());
            transfer.setMainId(cashOut.getMemberId());
            transfer.setMoney(cashOut.getMoney());
            transfer.setTradeType("member_cash_out");
            transfer.setRemark("");
            cashOut.setTransferNo(coreTransferService.create(transfer));
            memberCashOutMapper.updateById(cashOut);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("transfer_voucher", ObjectUtil.defaultIfEmpty(param.getTransferVoucher(), ""));
        data.put("transfer_remark", ObjectUtil.defaultIfEmpty(param.getTransferRemark(), ""));

        String transferType = ObjectUtil.defaultIfNull(param.getTransferType(), cashOut.getTransferType());

        if (!transferType.equals(TransferTypeEnum.OFFLINE.getKey())) {
            data.put("transfer_type", cashOut.getTransferType());
            data.put("transfer_realname", cashOut.getTransferRealname());
            data.put("transfer_mobile", cashOut.getTransferMobile());
            data.put("transfer_bank", cashOut.getTransferBank());
            data.put("transfer_account", cashOut.getTransferAccount());

            if (cashOut.getTransferType().equals(TransferTypeEnum.WECHATPAY.getKey())) {
                Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("wx_openid, weapp_openid").eq("member_id", cashOut.getMemberId()));
                data.put("openid", ObjectUtil.defaultIfEmpty(member.getWxOpenid(), member.getWeappOpenid()));
            }
        }

        coreTransferService.transfer(cashOut.getSiteId(), cashOut.getTransferNo(), transferType, data);
    }

    /**
     * 提现转账完成
     * @param transferNo
     */
    @Override
    public void transferFinish(String transferNo) {
        MemberCashOut cashOut = memberCashOutMapper.selectOne(new QueryWrapper<MemberCashOut>().eq("transfer_no", transferNo));
        Assert.notNull(cashOut, "提现记录不存在");
        if (!cashOut.getStatus().equals(MemberCashOutStatusEnum.WAIT_TRANSFER.getStatus())) throw new CommonException("当前提现申请未处于待转账状态");

        cashOut.setTransferTime(System.currentTimeMillis() / 1000);
        cashOut.setStatus(MemberCashOutStatusEnum.TRANSFERED.getStatus());
        memberCashOutMapper.updateById(cashOut);

        this.give(cashOut);
    }

    /**
     * 拒绝
     * @param siteId
     * @param cashOut
     * @param param
     */
    public void refuse(Integer siteId, MemberCashOut cashOut, MemberCashOutAuditParam param) {
        cashOut.setAuditTime(System.currentTimeMillis() / 1000);
        cashOut.setStatus(MemberCashOutStatusEnum.REFUSE.getStatus());
        cashOut.setRefuseReason(param.getRefuseReason() == null ? "": param.getRefuseReason());
        memberCashOutMapper.updateById(cashOut);

        this.giveback(cashOut);
    }

    /**
     * 累减提现中金额
     * @param cashOut
     */
    public void give(MemberCashOut cashOut) {
        Member member = memberMapper.selectById(cashOut.getMemberId());

        Member updateMember = new Member();
        updateMember.setMemberId(member.getMemberId());
        if (cashOut.getAccountType().equals(AccountTypeEnum.MONEY.getType())) {
            updateMember.setMoneyCashOuting(member.getMoneyCashOuting().subtract(cashOut.getApplyMoney()));
        }
        if (cashOut.getAccountType().equals(AccountTypeEnum.COMMISSION.getType())) {
            updateMember.setCommissionCashOuting(member.getCommissionCashOuting().subtract(cashOut.getApplyMoney()));
        }
        memberMapper.updateById(updateMember);
    }

    /**
     * 返还账户 累减提现中金额
     * @param cashOut
     */
    public void giveback(MemberCashOut cashOut) {
        Member member = memberMapper.selectById(cashOut.getMemberId());

        // 扣除对应账户金额
        coreMemberAccountService.addLog(cashOut.getSiteId(), cashOut.getMemberId(), cashOut.getAccountType(), cashOut.getApplyMoney().doubleValue(), "cash_out", "提现被拒绝,返还零钱", cashOut.getId().toString());

        Member updateMember = new Member();
        updateMember.setMemberId(member.getMemberId());
        if (cashOut.getAccountType().equals(AccountTypeEnum.MONEY.getType())) {
            updateMember.setMoneyCashOuting(member.getMoneyCashOuting().subtract(cashOut.getApplyMoney()));
        }
        if (cashOut.getAccountType().equals(AccountTypeEnum.COMMISSION.getType())) {
            updateMember.setCommissionCashOuting(member.getCommissionCashOuting().subtract(cashOut.getApplyMoney()));
        }
        memberMapper.updateById(updateMember);
    }
}
