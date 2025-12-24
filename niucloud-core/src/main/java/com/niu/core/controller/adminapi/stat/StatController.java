package com.niu.core.controller.adminapi.stat;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.stat.IStatService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jdk.jfr.Description;

/**
 * 小时统计控制器
 */

@RestController
@RequestMapping("adminapi/stat")
@SaCheckLogin
@Description("小时统计控制器")
public class StatController {

    @Resource
    IStatService statService;


    @GetMapping("/index")
    @Description("首页统计数据")
    public Result index() {
        return Result.success(statService.getIndexData());
    }

}
