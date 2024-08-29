package com.niu.core.controller.adminapi.notice;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.ISysNoticeLogService;
import com.niu.core.service.admin.sys.param.SysNoticeLogParam;
import com.niu.core.service.admin.sys.param.SysNoticeLogSearchParam;
import com.niu.core.service.admin.sys.vo.SysNoticeLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysNoticeLogListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 通知记录控制器
 */

@RestController
@RequestMapping("adminapi/notice/log")
@SaCheckLogin
public class NoticeLogController {

    @Resource
    ISysNoticeLogService sysNoticeLogService;

    /**
     * 通知记录列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SysNoticeLogListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SysNoticeLogListVo>> list(@Validated PageParam pageParam,
                                                     @Validated SysNoticeLogSearchParam searchParam) {
        PageResult<SysNoticeLogListVo> list = sysNoticeLogService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 通知记录详情
     * @param id 主键ID
     * @return Result<SysNoticeLogInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SysNoticeLogInfoVo> info(@Validated @RequestParam("id") Integer id) {
        SysNoticeLogInfoVo info = sysNoticeLogService.info(id);
        return Result.success(info);
    }
}
