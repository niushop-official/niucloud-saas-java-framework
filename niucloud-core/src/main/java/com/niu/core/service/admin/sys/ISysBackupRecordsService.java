package com.niu.core.service.admin.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysBackupRecords;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.BackupTaskVo;
import com.niu.core.service.admin.sys.vo.SysBackupRecordsListVo;

public interface ISysBackupRecordsService {

    PageResult<SysBackupRecordsListVo> page(PageParam pageParam, SysBackupRecordsSearchParam searchParam);

    void add(SysBackupRecordsParam addParam);

    void edit(Integer id, SysBackupRecordsParam editParam);

    /**
     * 清除
     * @param threshold
     */
    void clear(long threshold);

    void del(SysBackupRecordsDelParam delParam);

    /**
     * 恢复备份
     */
    BackupTaskVo restore(BackupRestoreParam param);

    /**
     * 备份
     */
    BackupTaskVo backup();

    /**
     * 获取备份任务
     */
    BackupTaskVo getBackupTask();

    void clearBackupTaskCache(int delayed);

    void clearRestoreTaskCache(int delayed);

    /**
     * 获取恢复任务
     */
    BackupTaskVo getRestoreTask();

    void setBackupRestoreTaskCache(BackupTaskVo vo);

    /**
     * 检测备份文件是否存在
     * @param param
     */
    SysBackupRecords checkDir(BackupRestoreParam param);

    /**
     * 检测目录权限
     * @return
     */
    JSONObject checkPermission();
}
