package com.niu.core.controller.adminapi.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.member.MemberCashOutStatusEnum;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.service.admin.member.IMemberCashOutService;
import com.niu.core.service.admin.member.param.*;
import com.niu.core.service.admin.member.vo.CashOutStatVo;
import com.niu.core.service.admin.member.vo.MemberCashOutInfoVo;
import com.niu.core.service.admin.member.vo.MemberCashOutListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.Map;
import jdk.jfr.Description;

/**
 * 会员提现控制器
 */
@Description("会员提现控制器")
@RestController
@RequestMapping("adminapi/member/cash_out")
@SaCheckLogin
public class MemberCashOutController {

    @Resource
    IMemberCashOutService memberCashOutService;

    /**
     * 会员提现列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MemberCashOutListVo>>
     */
    @Description("会员提现列表")
    @GetMapping("")
    public Result<PageResult<MemberCashOutListVo>> pages(@Validated PageParam pageParam, @Validated MemberCashOutSearchParam searchParam) {
        PageResult<MemberCashOutListVo> list = memberCashOutService.pages(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 会员提现详情
     * @param id 主键ID
     * @return Result<MemberCashOutInfoVo>
     */
    @Description("会员提现详情")
    @GetMapping("/{id}")
    public Result<MemberCashOutInfoVo> info(@PathVariable("id") Integer id) {
        MemberCashOutInfoVo info = memberCashOutService.info(id);
        return Result.success(info);
    }

    /**
     * 提现状态
     */
    @Description("获取提现状态")
    @GetMapping("/status")
    public Result<Map> getStatus() {
        return Result.success(MemberCashOutStatusEnum.getMap());
    }

    /**
     * 会员提现审核
     * @param id
     * @param action
     */
    @Description("会员提现审核")
    @PutMapping("/audit/{id}/{action}")
    public Result audit(@PathVariable(name = "id") Integer id, @PathVariable(name = "action") String action, @RequestBody MemberCashOutAuditParam param) {
        param.setId(id);
        param.setAction(action);
        memberCashOutService.audit(param);
        return Result.success();
    }

    /**
     * 会员提现取消
     * @param id
     * @return
     */
    @Description("会员提现取消")
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable(name = "id") Integer id) {
        memberCashOutService.cancel(id);
        return Result.success();
    }

    /**
     * 备注
     * @param id
     * @return
     */
    @Description("添加备注")
    @PutMapping("/remark/{id}")
    public Result remark(@PathVariable(name = "id") Integer id, @RequestBody MemberCashOutRemarkParam param) {
        memberCashOutService.remark(id, param);
        return Result.success();
    }

    /**
     * 转账方式
     */
    @Description("获取转账方式")
    @GetMapping("/transfertype")
    public Result<Map> getTransferType() {
        return Result.success(TransferTypeEnum.getMap());
    }

    /**
     * 转账
     * @param param
     * @return
     */
    @Description("转账处理")
    @PutMapping("/transfer/{id}")
    public Result transfer(@PathVariable("id") Integer id, @RequestBody CashOutTransferParam param) {
        param.setId(id);
        memberCashOutService.transfer(param);
        return Result.success();
    }

    /**
     * 提现统计
     */
    @Description("提现统计")
    @GetMapping("/stat")
    public Result<CashOutStatVo> stat() {
        CashOutStatVo data = memberCashOutService.stat();
        return Result.success(data);
    }

    /**
     * 检测转账状态
     * @param id
     * @return
     */
    @Description("检测转账状态")
    @PutMapping("/check/{id}")
    public Result checkTransferStatus(@PathVariable("id") Integer id) {
        memberCashOutService.checkTransferStatus(id);
        return Result.success();
    }
}
