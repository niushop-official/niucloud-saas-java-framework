package com.niu.core.service.core.member;

import com.niu.core.entity.member.MemberCashOut;
import com.niu.core.service.admin.member.param.CashOutTransferParam;
import com.niu.core.service.admin.member.param.MemberCashOutAuditParam;
import com.niu.core.service.core.member.param.MemberCashOutApplyParam;

/**
 * 会员提现core服务层
 */
public interface ICoreMemberCashOutService {

    /**
     * 申请提现
     * @param param
     */
    Integer apply(MemberCashOutApplyParam param);

    /**
     * 提现审核
     * @param siteId
     * @param id
     * @param action
     * @param param
     */
    void audit(Integer siteId, Integer id, String action, MemberCashOutAuditParam param);

    /**
     * 同意
     * @param siteId
     * @param cashOut
     * @param param
     */
    void agree(Integer siteId, MemberCashOut cashOut, MemberCashOutAuditParam param);

    /**
     * 拒绝
     * @param siteId
     * @param cashOut
     * @param param
     */
    void refuse(Integer siteId, MemberCashOut cashOut, MemberCashOutAuditParam param);

    /**
     * 转账成功
     * @param cashOut
     */
    Object transfer(MemberCashOut cashOut, CashOutTransferParam param);

    /**
     * 转账完成
     * @param transferNo
     */
    void transferFinish(String transferNo);

    /**
     *
     * 返还账户 累减提现中金额
     * @param cashOut
     */
    void give(MemberCashOut cashOut);

    /**
     *
     * 返还账户 累减提现中金额
     * @param cashOut
     */
    void giveback(MemberCashOut cashOut);

    /**
     * 撤销提现
     * @param siteId
     * @param id
     */
    void cancel(Integer siteId, Integer id);

    /**
     * 检测转账状态
     * @param siteId
     * @param id
     */
    void checkTransferStatus(Integer siteId, Integer id);
}
