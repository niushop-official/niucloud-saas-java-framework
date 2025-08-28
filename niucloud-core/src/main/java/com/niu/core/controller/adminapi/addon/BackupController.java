package com.niu.core.controller.adminapi.addon;


import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISysBackupRecordsService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.SysBackupRecordsListVo;
import com.niu.core.service.admin.sys.vo.SysUpgradeRecordsListVo;
import jakarta.annotation.Resource;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 在线升级
 */
@Description("在线备份")
@RestController
@RequestMapping("/adminapi/backup")
@SaCheckLogin
public class BackupController {

    @Resource
    private ISysBackupRecordsService sysBackupRecordsService;

    /**
     * 备份记录列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Description("备份记录列表")
    @GetMapping("/records")
    public Result<PageResult<SysBackupRecordsListVo>> list(@Validated PageParam pageParam,
                                                           @Validated SysBackupRecordsSearchParam searchParam) {
        return Result.success(sysBackupRecordsService.page(pageParam,searchParam));
    }

    @Description("删除备份记录")
    @PostMapping("/delete")
    public Result delete(@Validated @RequestBody SysBackupRecordsDelParam delParam) {
        sysBackupRecordsService.del(delParam);
        return Result.success();
    }

    @Description("备注添加")
    @PutMapping("/remark")
    public Result remark(@Validated @RequestBody SysBackupRecordsUpdateParam param) {
        SysBackupRecordsParam sysBackupRecordsParam = new SysBackupRecordsParam();
        sysBackupRecordsParam.setRemark(param.getRemark());
        sysBackupRecordsService.edit(param.getId(), sysBackupRecordsParam);
        return Result.success();
    }


    @Description("恢复备份")
    @PostMapping("/restore")
    public Result restore(@RequestBody BackupRestoreParam param) {
        return Result.success(sysBackupRecordsService.restore(param));
    }

    @Description("手动备份")
    @PostMapping("/manual")
    public Result manual() {
        return Result.success(sysBackupRecordsService.backup());
    }

    @Description("获取备份任务")
    @GetMapping("/task")
    public Result getBackupTask() {
        return Result.success(sysBackupRecordsService.getBackupTask());
    }

    @Description("获取恢复任务")
    @GetMapping("/restore_task")
    public Result getRestoreTask() {
        return Result.success(sysBackupRecordsService.getRestoreTask());
    }

    @Description("检测备份文件是否存在")
    @PostMapping("/check_dir")
    public Result checkDir(@RequestBody BackupRestoreParam param) {
        sysBackupRecordsService.checkDir(param);
        return Result.success();
    }

    @Description("检测目录权限")
    @PostMapping("/check_permission")
    public Result checkPermission() {
        return Result.success(sysBackupRecordsService.checkPermission());
    }
}
