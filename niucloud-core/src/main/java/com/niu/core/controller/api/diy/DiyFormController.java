package com.niu.core.controller.api.diy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.enums.HttpEnum;
import com.niu.core.service.api.diy.IDiyFormService;
import com.niu.core.service.api.diy.vo.DiyMemberRecordVo;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/diy/form")
@Slf4j
public class DiyFormController {

    @Resource
    private IDiyFormService diyFormService;

    /**
     * 获取表单信息
     * @param formId
     * @return
     */
    @GetMapping("")
    public Result<?> info(@RequestParam(name="form_id", required = true) Integer formId) {
        return Result.success(diyFormService.getInfo(formId));
    }

    @SaCheckLogin
    @GetMapping("/record")
    public Result<?> getRecord(@RequestParam(name="record_id", required = false) Integer recordId) {
        return Result.success(diyFormService.getFormRecordInfo(recordId));
    }

    @GetMapping("/result")
    public Result<?> getResult(@RequestParam(name="record_id", required = true) Integer recordId) {
        return Result.success(diyFormService.getResult(recordId));
    }

    @SaCheckLogin
    @PostMapping("/record")
    public Result<?> addRecord(@RequestBody DiyFormRecordsParam param) {
        return Result.success(HttpEnum.SUCCESS.getCode(), HttpEnum.SUCCESS.getMsg(), diyFormService.addRecord(param));
    }

    @SaCheckLogin
    @PutMapping("/record")
    public Result<?> editRecord(@RequestBody DiyFormRecordsParam param) {
        diyFormService.editRecord(param);
        return Result.success();
    }

    /**
     * 获取个人资料表单填写记录
     * @return
     */
    @SaCheckLogin
    @GetMapping("/member_record")
    public Result<?> getMemberInfoRecord() {
        return Result.success(diyFormService.getMemberInfoRecord());
    }

}
