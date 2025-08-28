package com.niu.recharge.controller.adminapi;

import jdk.jfr.Description;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.recharge.service.admin.IRechargeService;
import com.niu.recharge.service.admin.param.RechargeEditSortParam;
import com.niu.recharge.service.admin.param.RechargeModifyStatusParam;
import com.niu.recharge.service.admin.param.RechargePageParam;
import com.niu.recharge.service.admin.param.RechargeParam;
import com.niu.recharge.service.core.param.RechargeSetConfigParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("adminapi/recharge")
@Description("充值套餐管理")
public class RechargeController {
    private final IRechargeService rechargeService;

    @PostMapping("/package/config")
    @Description("设置充值配置")
    Result<?> setRechargeConfig(@RequestBody RechargeSetConfigParam param) {
        rechargeService.setRechargeConfig(param);
        return Result.success();
    }

    @GetMapping("/package/config")
    @Description("获取充值配置")
    Result<?> getRechargeConfig() {
        return Result.success(rechargeService.getRechargeConfig());
    }

    @GetMapping("/package")
    @Description("充值套餐列表")
    Result<?> lists(PageParam pageParam, RechargePageParam param) {
        return Result.success(rechargeService.getPage(pageParam, param));
    }

    @PostMapping("/package")
    @Description("添加充值套餐")
    Result<?> add(@RequestBody RechargeParam param) {
        rechargeService.add(param);
        return Result.success();
    }

    @PutMapping("/package/{id}")
    @Description("编辑充值套餐")
    Result<?> edit(@PathVariable("id") Integer id, @RequestBody RechargeParam param) {
        rechargeService.edit(id, param);
        return Result.success();
    }

    @GetMapping("/package/init")
    @Description("获取充值套餐初始信息")
    Result<?> init(@RequestParam("recharge_id") Integer rechargeId) {
        return Result.success(rechargeService.getInit(rechargeId));
    }

    @DeleteMapping("/package/{id}")
    @Description("删除充值套餐")
    Result<?> del(@PathVariable("id") Integer id) {
        rechargeService.del(id);
        return Result.success();
    }

    @PutMapping("/package/status")
    @Description("修改充值套餐状态")
    Result<?> modifyStatus(@RequestBody RechargeModifyStatusParam param) {
        rechargeService.modifyStatus(param);
        return Result.success();
    }

    @PutMapping("/package/sort")
    @Description("编辑充值套餐排序")
    Result<?> editSort(@RequestBody RechargeEditSortParam param) {
        rechargeService.editSort(param);
        return Result.success();
    }

    @GetMapping("dict/package_gift")
    @Description("获取充值套餐礼品字典")
    Result<?> getPackageGiftDict() {
        return Result.success(rechargeService.getPackageGiftDict());
    }

}
