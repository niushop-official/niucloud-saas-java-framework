package com.niu.core.controller.adminapi.notice;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.ISysNoticeSmsLogService;
import com.niu.core.service.admin.sys.param.SysNoticeSmsLogParam;
import com.niu.core.service.admin.sys.param.SysNoticeSmsLogSearchParam;
import com.niu.core.service.admin.sys.vo.SysNoticeSmsLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysNoticeSmsLogListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 短信发送控制器
 */

@RestController
@RequestMapping("adminapi/notice/sms/log")
@SaCheckLogin
public class NoticeSmsLogController {

    @Resource
    ISysNoticeSmsLogService sysNoticeSmsLogService;

    /**
     * 短信发送列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SysNoticeSmsLogListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SysNoticeSmsLogListVo>> list(@Validated PageParam pageParam,
                                                     @Validated SysNoticeSmsLogSearchParam searchParam) {
        PageResult<SysNoticeSmsLogListVo> list = sysNoticeSmsLogService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 短信发送详情
     * @param id 主键ID
     * @return Result<SysNoticeSmsLogInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SysNoticeSmsLogInfoVo> info(@Validated @RequestParam("id") Integer id) {
        SysNoticeSmsLogInfoVo info = sysNoticeSmsLogService.info(id);
        return Result.success(info);
    }
}
