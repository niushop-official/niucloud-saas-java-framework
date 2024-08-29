package com.niu.core.controller.adminapi.stat;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.stat.IStatHourService;
import com.niu.core.service.admin.stat.IStatService;
import com.niu.core.service.admin.stat.param.StatHourParam;
import com.niu.core.service.admin.stat.param.StatHourSearchParam;
import com.niu.core.service.admin.stat.vo.StatHourInfoVo;
import com.niu.core.service.admin.stat.vo.StatHourListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 小时统计控制器
 */

@RestController
@RequestMapping("adminapi/stat")
@SaCheckLogin
public class StatController {

    @Resource
    IStatService statService;


    @GetMapping("/index")
    public Result index() {
        return Result.success(statService.getIndexData());
    }

}
