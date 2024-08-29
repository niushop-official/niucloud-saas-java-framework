package com.niu.core.service.api.member;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.AccountPointCountVo;

public interface IMemberAccountService {

    /**
     * 积分流水
     *
     * @param param
     * @return
     */
    PageResult<?> pointFlow(PageParam pageParam, AccountPointFlowParam param);

    /**
     * 余额流水
     *
     * @param param
     * @return
     */
    PageResult<?> balanceFlow(PageParam pageParam, AccountBalanceFlowParam param);

    /**
     * 余额流水（新）
     *
     * @param param
     * @return
     */
    PageResult<?> balanceList(PageParam pageParam, AccountBalanceListParam param);

    /**
     * 零钱流水
     *
     * @param param
     * @return
     */
    PageResult<?> moneyFlow(PageParam pageParam, AccountMoneyFlowParam param);

    /**
     * 账户记录数量
     *
     * @param param
     * @return
     */
    Long accountRecords(AccountRecordsParam param);

    /**
     * 佣金流水
     *
     * @param param
     * @return
     */
    PageResult<?> commissionFlow(PageParam pageParam, AccountCommissionFlowParam param);

    /**
     * 账户来源
     *
     * @param param
     * @return
     */
    JSONObject getAccountSource(AccountAccountSourceParam param);

    /**
     * 积分数量
     *
     * @param param
     * @return
     */
    AccountPointCountVo pointCount(AccountPointCountParam param);

}
