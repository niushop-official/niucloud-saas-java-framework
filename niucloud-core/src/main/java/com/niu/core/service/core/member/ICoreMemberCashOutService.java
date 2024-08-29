package com.niu.core.service.core.member;

import com.niu.core.entity.member.MemberCashOut;
import com.niu.core.service.admin.member.param.MemberCashOutAuditParam;
import com.niu.core.service.core.member.param.WechatConfigParam;
import com.niu.core.service.core.member.vo.WechatConfigVo;

/**
 * 会员提现core服务层
 */
public interface ICoreMemberCashOutService {
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
}
