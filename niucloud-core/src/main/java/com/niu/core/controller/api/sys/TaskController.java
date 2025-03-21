package com.niu.core.controller.api.sys;

import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.system.CaptchaUtils;
import com.niu.core.service.api.sys.ITaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Resource
    ITaskService taskService;

    @GetMapping("/task/growth")
    public Result growth() {
        return Result.success(taskService.getGrowthTask());
    }

    @GetMapping("/task/point")
    public Result point() {
        return Result.success(taskService.getPointTask());
    }

}
