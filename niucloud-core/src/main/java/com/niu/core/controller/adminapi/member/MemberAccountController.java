package com.niu.core.controller.adminapi.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.member.MemberAccountChangeTypeEnum;
import com.niu.core.service.admin.member.IMemberAccountService;
import com.niu.core.service.admin.member.param.AdjustAccountParam;
import com.niu.core.service.admin.member.param.MemberAccountLogSearchParam;
import com.niu.core.service.admin.member.vo.*;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Map;

/**
 * 会员账单控制器
 */

@RestController
@RequestMapping("adminapi/member/account")
@SaCheckLogin
public class MemberAccountController {

    @Resource
    IMemberAccountService memberAccountService;

    /**
     * 获取账户类型
     * @return
     */
    @GetMapping("/type")
    public Result<Map> accountType() {
        return Result.success(AccountTypeEnum.getMap());
    }

    /**
     * 会员积分流水
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MemberAccountLogListVo>>
     */
    @GetMapping("/point")
    public Result<PageResult<MemberAccountLogListVo>> point(@Validated PageParam pageParam, @Validated MemberAccountLogSearchParam searchParam) {
        searchParam.setAccountType(AccountTypeEnum.POINT.getType());
        PageResult<MemberAccountLogListVo> list = memberAccountService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 会员余额流水
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MemberAccountLogListVo>>
     */
    @GetMapping("/balance")
    public Result<PageResult<MemberAccountLogListVo>> balance(@Validated PageParam pageParam, @Validated MemberAccountLogSearchParam searchParam) {
        searchParam.setAccountType(AccountTypeEnum.BALANCE.getType());
        PageResult<MemberAccountLogListVo> list = memberAccountService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 会员可提现余额流水
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MemberAccountLogListVo>>
     */
    @GetMapping("/money")
    public Result<PageResult<MemberAccountLogListVo>> money(@Validated PageParam pageParam, @Validated MemberAccountLogSearchParam searchParam) {
        searchParam.setAccountType(AccountTypeEnum.MONEY.getType());
        PageResult<MemberAccountLogListVo> list = memberAccountService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 会员成长值流水
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MemberAccountLogListVo>>
     */
    @GetMapping("/growth")
    public Result<PageResult<MemberAccountLogListVo>> growth(@Validated PageParam pageParam, @Validated MemberAccountLogSearchParam searchParam) {
        searchParam.setAccountType(AccountTypeEnum.GROWTH.getType());
        PageResult<MemberAccountLogListVo> list = memberAccountService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 会员佣金流水
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MemberAccountLogListVo>>
     */
    @GetMapping("/commission")
    public Result<PageResult<MemberAccountLogListVo>> commission(@Validated PageParam pageParam, @Validated MemberAccountLogSearchParam searchParam) {
        searchParam.setAccountType(AccountTypeEnum.COMMISSION.getType());
        PageResult<MemberAccountLogListVo> list = memberAccountService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 调整积分
     */
    @PostMapping("/point")
    public Result<Object> adjustPoint(@Validated @RequestBody AdjustAccountParam param) {
        memberAccountService.adjustPoint(param);
        return Result.success();
    }

    /**
     * 调整余额
     */
    @PostMapping("/balance")
    public Result<Object> adjustBalance(@Validated @RequestBody AdjustAccountParam param) {
        memberAccountService.adjustBalance(param);
        return Result.success();
    }

    /**
     * 会员佣金统计
     */
    @GetMapping("/sum_commission")
    public Result<SumCommissionVo> sumCommission(@Validated MemberAccountLogSearchParam searchParam) {
        SumCommissionVo data = memberAccountService.sumCommission(searchParam);
        return Result.success(data);
    }

    /**
     * 会员积分统计
     */
    @GetMapping("/sum_point")
    public Result<SumPointVo> sumPoint(@Validated MemberAccountLogSearchParam searchParam) {
        SumPointVo data = memberAccountService.sumPoint(searchParam);
        return Result.success(data);
    }

    /**
     * 会员积分统计
     */
    @GetMapping("/sum_balance")
    public Result<SumBalanceVo> sumBalance(@Validated MemberAccountLogSearchParam searchParam) {
        SumBalanceVo data = memberAccountService.sumBalance(searchParam);
        return Result.success(data);
    }

    /**
     * 获取账户类型的变动方式
     * @param accountType
     * @return
     */
    @GetMapping("/change_type/{account_type}")
    public Result<JSONObject> changeType(@PathVariable("account_type") String accountType) {
        return Result.success(MemberAccountChangeTypeEnum.getType(accountType));
    }

}
