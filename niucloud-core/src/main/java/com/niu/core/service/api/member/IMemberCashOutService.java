package com.niu.core.service.api.member;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.vo.CashOutConfigVo;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.*;
import com.niu.core.service.core.member.param.MemberCashOutApplyParam;

public interface IMemberCashOutService {

    /**
     * 会员提现列表
     *
     * @param param
     * @return
     */
    PageResult<?> cashOutList(PageParam pageParam, MemberCashOutListParam param);

    /**
     * 会员提现详情
     *
     * @param param
     * @return
     */
    MemberCashOutInfoVo cashOutInfo(MemberCashOutInfoParam param);

    /**
     * 提现配置
     *
     * @param param
     * @return
     */
    CashOutConfigVo cashOutConfig(MemberCashOutConfigParam param);

    /**
     * 提现转账方式
     *
     * @param param
     * @return
     */
    Object transferMethod(MemberCashOutTransferMethodParam param);

    /**
     * 提现申请
     *
     * @param param
     * @return
     */
    boolean cashOutApply(MemberCashOutApplyParam param);

    /**
     * 撤销提现申请
     *
     * @param param
     * @return
     */
    boolean cashOutCancel(MemberCashOutCancelParam param);

    /**
     * 提现账号列表
     *
     * @param param
     * @return
     */
    PageResult<?> accountList(PageParam pageParam, MemberCashOutAccountListParam param);

    /**
     * 提现账户详情
     *
     * @param param
     * @return
     */
    MemberCashOutAccountDetailsVo accountDetails(MemberCashOutAccountDetailsParam param);

    /**
     * 首条提现账号详情
     *
     * @param param
     * @return
     */
    MemberCashOutFirstAccountDetailsVo accountDetailsOfFirst(MemberCashOutFirstAccountDetailsParam param);

    /**
     * 添加提现账号
     *
     * @param param
     * @return
     */
    Integer addAccount(MemberCashOutAccountAddParam param);

    /**
     * 编辑提现账号
     *
     * @param accountId
     * @param param
     * @return
     */
    Integer editAccount(Integer accountId, MemberCashOutAccountEditParam param);

    /**
     * 删除提现账号
     *
     * @param param
     * @return
     */
    Integer deleteAccount(MemberCashOutAccountDeleteParam param);

}
