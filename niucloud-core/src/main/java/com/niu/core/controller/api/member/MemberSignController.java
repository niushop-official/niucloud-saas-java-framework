package com.niu.core.controller.api.member;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.object.ObjectUtils;
import com.niu.core.service.api.member.IMemberSignService;
import com.niu.core.service.api.member.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 会员签到
 */
@RestController
@RequestMapping("/api/member")
@Slf4j
public class MemberSignController {

    @Autowired
    private IMemberSignService memberSignService;

    /**
     * 会员签到记录
     *
     * @return
     */
    @GetMapping("/sign")
    public Result<?> lists(PageParam pageParam, MemberSignRecordParam param) {
        return Result.success(memberSignService.signRecord(pageParam, param));
    }

    /**
     * 签到详情
     *
     * @param signId
     * @return
     */
    @GetMapping("/sign/{sign_id}")
    public Result<?> info(@PathVariable("sign_id") Integer signId) {
        MemberSignDetailsParam param = ObjectUtils.create(MemberSignDetailsParam.class);
        param.setSignId(signId);
        return Result.success(memberSignService.signDetails(param));
    }

    /**
     * 会员签到
     *
     * @return
     */
    @PostMapping("/sign")
    public Result<?> sign() {
        MemberSignOperateParam param = ObjectUtils.create(MemberSignOperateParam.class);
        return Result.success(memberSignService.signOperate(param));
    }

    /**
     * 获取月签到数据
     *
     * @param year
     * @param month
     * @return
     */
    @GetMapping("/sign/info/{year}/{month}")
    public Result<?> signInfo(@PathVariable("year") String year, @PathVariable("month") String month) {
        MemberSignMonthRecordParam param = ObjectUtils.create(MemberSignMonthRecordParam.class);
        param.setYear(year);
        param.setMonth(month);
        return Result.success(memberSignService.signMonthRecord(param));
    }

    /**
     * 获取日签到奖励
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    @GetMapping("/sign/award/{year}/{month}/{day}")
    public Result<?> getDayAward(@PathVariable("year") String year, @PathVariable("month") String month, @PathVariable("month") String day) {
        MemberSignDayRecordParam param = ObjectUtils.create(MemberSignDayRecordParam.class);
        param.setYear(year);
        param.setMonth(month);
        param.setDay(day);
        return Result.success(memberSignService.signDayRecord(param));
    }

    /**
     * 获取签到设置
     *
     * @return
     */
    @GetMapping("/sign/config")
    public Result<?> signConfig() {
        MemberSignConfigParam param = ObjectUtils.create(MemberSignConfigParam.class);
        return Result.success(memberSignService.signConfig(param));
    }

}
