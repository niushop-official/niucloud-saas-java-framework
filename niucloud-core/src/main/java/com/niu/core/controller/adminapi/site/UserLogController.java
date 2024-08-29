package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISysUserLogService;
import com.niu.core.service.admin.sys.param.SysUserLogParam;
import com.niu.core.service.admin.sys.param.SysUserLogSearchParam;
import com.niu.core.service.admin.sys.vo.SysUserLogListVo;
import com.niu.core.service.admin.sys.vo.SysUserLogInfoVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 管理员操作记录管理
 */
@RestController
@RequestMapping("/adminapi/site/")
@SaCheckLogin
public class UserLogController {

    @Resource
    ISysUserLogService sysUserLogService;

    /**
     * 管理员操作记录列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return Result<Object>
     */
    @GetMapping("/log")
    public Result<PageResult<SysUserLogListVo>> list(@Validated PageParam pageParam,
                                                         @Validated SysUserLogSearchParam searchParam) {
        PageResult<SysUserLogListVo> list = sysUserLogService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 管理员操作记录详情
     *
     * @param id 主键ID
     * @return Result<SysUserLogInfoVo>
     */
    @GetMapping("/log/{id}")
    public Result<SysUserLogInfoVo> info(@Validated @PathVariable("id") Integer id) {
        SysUserLogInfoVo info = sysUserLogService.info(id);
        return Result.success(info);
    }

}
