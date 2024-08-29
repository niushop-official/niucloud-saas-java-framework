package com.niu.core.service.core.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.member.MemberCashOut;
import com.niu.core.enums.member.MemberCashOutStatusEnum;
import com.niu.core.mapper.member.MemberCashOutMapper;
import com.niu.core.service.admin.member.param.MemberCashOutAuditParam;
import com.niu.core.service.core.member.ICoreMemberCashOutService;
import com.niu.core.service.core.member.param.WechatConfigParam;
import com.niu.core.service.core.member.vo.WechatConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@Service
public class CoreMemberCashOutServiceImpl implements ICoreMemberCashOutService {

    @Resource
    MemberCashOutMapper memberCashOutMapper;

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
    }
}
