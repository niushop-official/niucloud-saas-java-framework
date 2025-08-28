package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISysScheduleService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.SysScheduleInfoVo;
import com.niu.core.service.admin.sys.vo.SysScheduleListVo;
import com.niu.core.service.admin.sys.vo.SysScheduleLogListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * 系统任务控制器
 */
@RestController
@RequestMapping("/adminapi/sys/schedule")
@SaCheckLogin
public class SysScheduleController {

    @Resource
    private ISysScheduleService sysScheduleService;

    /**
     * 系统任务列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < SysScheduleListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<SysScheduleListVo>> list(@Validated PageParam pageParam,
                                                      @Validated SysScheduleSearchParam searchParam) {
        PageResult<SysScheduleListVo> pageResult = sysScheduleService.list(pageParam, searchParam);
        return Result.success(pageResult);
    }

    /**
     * 系统任务详情
     *
     * @param id 主键ID
     * @return Result<SysScheduleInfoVo>
     */
    @GetMapping("/info/{id}")
    public Result<SysScheduleInfoVo> info(@Validated @RequestParam("id") Integer id) {
        SysScheduleInfoVo info = sysScheduleService.info(id);
        return Result.success(info);
    }

    /**
     * @return
     */
    @PutMapping("/modify/status/{id}")
    public Result status(@Validated @RequestParam("id") Integer id, SysScheduleStatusParam sysScheduleStatusParam) {
        sysScheduleStatusParam.setId(id);
        sysScheduleService.modifyStatus(sysScheduleStatusParam);
        return Result.success();
    }

    /**
     * 系统任务添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SysScheduleParam addParam) {
        sysScheduleService.add(addParam);
        return Result.success();
    }

    /**
     * 系统任务编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SysScheduleParam editParam) {
        sysScheduleService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 系统任务删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        sysScheduleService.del(id);
        return Result.success();
    }

    /**
     * 类型
     *
     * @return
     */
    @GetMapping("/type")
    public Result type() {
        return Result.success(sysScheduleService.type());
    }

    /**
     * 模板
     *
     * @return
     */
    @GetMapping("/template")
    public Result template() {
        return Result.success(sysScheduleService.template());
    }


    /**
     * 数据类型
     *
     * @return
     */
    @GetMapping("/datetype")
    public Result dateType() {
        return Result.success(sysScheduleService.dateType());
    }

    /**
     * 重置定时任务
     */
    @PostMapping("/reset")
    public Result reset() {
        sysScheduleService.resetSchedule();
        return Result.success();
    }

    /**
     * 计划任务执行记录分页列表
     */
    @GetMapping("/log/list")
    public Result logList(@Validated PageParam pageParam, @Validated SysScheduleLogSearchParam searchParam) {
        PageResult<SysScheduleLogListVo> pageResult = sysScheduleService.logList(pageParam, searchParam);
        return Result.success(pageResult);
    }

    /**
     * 执行一次任务
     */
    @PutMapping("/do/{id}")
    public Result doSchedule(@PathVariable("id") Integer id) {
        sysScheduleService.doSchedule(id);
        return Result.success();
    }

    /**
     * 删除日志
     */
    @PutMapping("/log/delete")
    public Result delLog(@RequestBody SysScheduleLogDelParam param) {
        sysScheduleService.delLog(param.getIds());
        return Result.success();
    }

    /**
     * 清空计划任务执行记录
     */
    @PutMapping("/log/clear")
    public Result clearLog(@RequestBody SysScheduleLogDelParam param) {
        sysScheduleService.clearLog(param.getScheduleId());
        return Result.success();
    }
}
