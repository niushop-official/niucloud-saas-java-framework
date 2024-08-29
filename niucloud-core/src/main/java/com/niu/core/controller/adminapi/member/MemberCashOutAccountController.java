package com.niu.core.controller.adminapi.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.IMemberCashOutAccountService;
import com.niu.core.service.admin.member.param.MemberCashOutAccountParam;
import com.niu.core.service.admin.member.param.MemberCashOutAccountSearchParam;
import com.niu.core.service.admin.member.vo.MemberCashOutAccountInfoVo;
import com.niu.core.service.admin.member.vo.MemberCashOutAccountListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 会员提现账户控制器
 */

@RestController
@RequestMapping("adminapi/member_cash_out_account")
@SaCheckLogin
public class MemberCashOutAccountController {

    @Resource
    IMemberCashOutAccountService memberCashOutAccountService;

    /**
     * 会员提现账户列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MemberCashOutAccountListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<MemberCashOutAccountListVo>> list(@Validated PageParam pageParam,
                                                     @Validated MemberCashOutAccountSearchParam searchParam) {
        PageResult<MemberCashOutAccountListVo> list = memberCashOutAccountService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 会员提现账户详情
     * @param id 主键ID
     * @return Result<MemberCashOutAccountInfoVo>
     */
    @GetMapping("/info")
    public Result<MemberCashOutAccountInfoVo> info(@Validated @RequestParam("id") Integer id) {
        MemberCashOutAccountInfoVo info = memberCashOutAccountService.info(id);
        return Result.success(info);
    }

    /**
     * 会员提现账户添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody MemberCashOutAccountParam addParam) {
        memberCashOutAccountService.add(addParam);
        return Result.success();
    }

    /**
     * 会员提现账户编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody MemberCashOutAccountParam editParam) {
        memberCashOutAccountService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 会员提现账户删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        memberCashOutAccountService.del(id);
        return Result.success();
    }

}
