package com.niu.core.service.admin.member;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.param.CashOutTransferParam;
import com.niu.core.service.admin.member.param.MemberCashOutAuditParam;
import com.niu.core.service.admin.member.param.MemberCashOutParam;
import com.niu.core.service.admin.member.param.MemberCashOutSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.member.vo.CashOutStatVo;
import com.niu.core.service.admin.member.vo.MemberCashOutInfoVo;
import com.niu.core.service.admin.member.vo.MemberCashOutListVo;

/**
 * 会员提现服务接口
 */
public interface IMemberCashOutService {
    /**
     * 会员提现列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberCashOutListVo>
     */
    PageResult<MemberCashOutListVo> pages(PageParam pageParam, MemberCashOutSearchParam searchParam);

    /**
     * 会员提现详情
     * @param id 主键ID
     * @return MemberCashOutInfoVo
     */
    MemberCashOutInfoVo info(Integer id);

    /**
     * 提现统计
     * @return
     */
    CashOutStatVo stat();

    /**
     * 提现审核
     * @param param
     */
    void audit(MemberCashOutAuditParam param);

    /**
     * 转账
     */
    void transfer(CashOutTransferParam param);
}
