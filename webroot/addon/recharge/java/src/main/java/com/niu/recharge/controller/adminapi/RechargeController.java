package com.niu.recharge.controller.adminapi;

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
public class RechargeController {
    private final IRechargeService rechargeService;

    @PostMapping("/package/config")
    Result<?> setRechargeConfig(@RequestBody RechargeSetConfigParam param) {
        rechargeService.setRechargeConfig(param);
        return Result.success();
    }

    @GetMapping("/package/config")
    Result<?> getRechargeConfig() {
        return Result.success(rechargeService.getRechargeConfig());
    }

    @GetMapping("/package")
    Result<?> lists(PageParam pageParam, RechargePageParam param) {
        return Result.success(rechargeService.getPage(pageParam, param));
    }

    @PostMapping("/package")
    Result<?> add(@RequestBody RechargeParam param) {
        rechargeService.add(param);
        return Result.success();
    }

    @PutMapping("/package/{id}")
    Result<?> edit(@PathVariable("id") Integer id, @RequestBody RechargeParam param) {
        rechargeService.edit(id, param);
        return Result.success();
    }

    @GetMapping("/package/init")
    Result<?> init(@RequestParam("recharge_id") Integer rechargeId) {
        return Result.success(rechargeService.getInit(rechargeId));
    }

    @DeleteMapping("/package/{id}")
    Result<?> del(@PathVariable("id") Integer id) {
        rechargeService.del(id);
        return Result.success();
    }

    @PutMapping("/package/status")
    Result<?> modifyStatus(@RequestBody RechargeModifyStatusParam param) {
        rechargeService.modifyStatus(param);
        return Result.success();
    }

    @PutMapping("/package/sort")
    Result<?> editSort(@RequestBody RechargeEditSortParam param) {
        rechargeService.editSort(param);
        return Result.success();
    }

    @GetMapping("dict/package_gift")
    Result<?> getPackageGiftDict() {
        return Result.success(rechargeService.getPackageGiftDict());
    }

}
