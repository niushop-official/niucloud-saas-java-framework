package com.niu.core.controller.api.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.object.ObjectUtils;
import com.niu.core.service.api.member.IMemberCashOutService;
import com.niu.core.service.api.member.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 会员提现
 */
@RestController
@RequestMapping("/api/member")
@Slf4j
@SaCheckLogin
public class MemberCashOutController {

    @Autowired
    private IMemberCashOutService memberCashOutService;

    /**
     * 会员提现列表
     *
     * @return
     */
    @GetMapping("/cash_out")
    public Result<?> lists(PageParam pageParam, MemberCashOutListParam param) {
        return Result.success(memberCashOutService.cashOutList(pageParam, param));
    }

    /**
     * 会员提现详情
     *
     * @return
     */
    @GetMapping("/cash_out/{id}")
    public Result<?> info(@PathVariable("id") Integer id) {
        MemberCashOutInfoParam param = ObjectUtils.create(MemberCashOutInfoParam.class);
        param.setId(id);
        return Result.success(memberCashOutService.cashOutInfo(param));
    }

    /**
     * 提现配置
     *
     * @return
     */
    @GetMapping("/cash_out/config")
    public Result<?> config() {
        MemberCashOutConfigParam param = ObjectUtils.create(MemberCashOutConfigParam.class);
        return Result.success(memberCashOutService.cashOutConfig(param));
    }

    /**
     * 提现转账方式
     *
     * @return
     */
    @GetMapping("/cash_out/transfertype")
    public Result<?> getTransferType() {
        MemberCashOutTransferMethodParam param = ObjectUtils.create(MemberCashOutTransferMethodParam.class);
        return Result.success(memberCashOutService.transferMethod(param));
    }

    /**
     * 提现申请
     *
     * @return
     */
    @PostMapping("/cash_out/cash_out/apply")
    public Result<?> apply(MemberCashOutApplyParam param) {
        memberCashOutService.cashOutApply(param);
        return Result.success();
    }

    /**
     * 撤销提现申请
     *
     * @return
     */
    @PutMapping("/cash_out/cancel/{id}")
    public Result<?> cancel(@PathVariable("id") Integer id) {
        MemberCashOutCancelParam param = ObjectUtils.create(MemberCashOutCancelParam.class);
        param.setId(id);
        memberCashOutService.cashOutCancel(param);
        return Result.success();
    }

    /**
     * 提现账号列表
     *
     * @return
     */
    @GetMapping("/cashout_account")
    public Result<?> accountList(PageParam pageParam, MemberCashOutAccountListParam param) {
        return Result.success(memberCashOutService.accountList(pageParam, param));
    }

    /**
     * 提现账号详情
     *
     * @return
     */
    @GetMapping("/cashout_account/{account_id}")
    public Result<?> accountInfo(@PathVariable("account_id") Integer accountId) {
        MemberCashOutAccountDetailsParam param = ObjectUtils.create(MemberCashOutAccountDetailsParam.class);
        param.setAccountId(accountId);
        return Result.success(memberCashOutService.accountDetails(param));
    }

    /**
     * 首条提现账号详情
     *
     * @return
     */
    @GetMapping("/cashout_account/firstinfo")
    public Result<?> firstInfo(MemberCashOutFirstAccountDetailsParam param) {
        return Result.success(memberCashOutService.accountDetailsOfFirst(param));
    }

    /**
     * 添加提现账号
     *
     * @return
     */
    @PostMapping("/cashout_account")
    public Result<?> add(@RequestBody MemberCashOutAccountAddParam param) {
        return Result.success(memberCashOutService.addAccount(param));
    }

    /**
     * 编辑提现账号
     *
     * @return
     */
    @PutMapping("/cashout_account/{account_id}")
    public Result<?> edit(@PathVariable("account_id") Integer accountId, @RequestBody MemberCashOutAccountEditParam param) {
        return Result.success(memberCashOutService.editAccount(accountId, param));
    }

    /**
     * 删除提现账号
     *
     * @return
     */
    @DeleteMapping("/cashout_account/{account_id}")
    public Result<?> delete(@PathVariable("account_id") Integer accountId) {
        MemberCashOutAccountDeleteParam param = ObjectUtils.create(MemberCashOutAccountDeleteParam.class);
        param.setAccountId(accountId);
        return Result.success(memberCashOutService.deleteAccount(param));
    }

}
