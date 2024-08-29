package com.niu.core.service.admin.member;

import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.member.param.AdjustAccountParam;
import com.niu.core.service.admin.member.param.MemberAccountLogParam;
import com.niu.core.service.admin.member.param.MemberAccountLogSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.member.vo.*;

/**
 * 会员账单服务接口
 */
public interface IMemberAccountService {


    /**
     * 会员账单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberAccountLogListVo>
     */
    PageResult<MemberAccountLogListVo> list(PageParam pageParam, MemberAccountLogSearchParam searchParam);

    /**
     * 佣金统计
     * @param searchParam
     */
    SumCommissionVo sumCommission(MemberAccountLogSearchParam searchParam);

    /**
     * 余额统计
     * @param searchParam
     */
    SumBalanceVo sumBalance(MemberAccountLogSearchParam searchParam);

    /**
     * 积分统计
     * @param searchParam
     */
    SumPointVo sumPoint(MemberAccountLogSearchParam searchParam);

    /**
     *
     * @param memberId
     * @return
     */
    MemberAccountVo getMemberAccountInfo(Integer memberId);

    void adjustPoint(AdjustAccountParam param);

    void adjustBalance(AdjustAccountParam param);
}
