package com.niu.core.job.upgrade;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.service.admin.sys.ISysBackupRecordsService;
import com.niu.core.service.admin.sys.ISysUpgradeRecordsService;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@JobProvider(key = "AutoClearUpgradeRecords", name = "定时清理升级/备份记录表", describe = "定时清理升级/备份记录表", source = "core")
public class AutoClearUpgradeRecords extends AbstractJobProvider implements IJobProvider {

    private static final int DAYS_TO_KEEP = 7;

    private ISysUpgradeRecordsService sysUpgradeRecordsService() {
        return SpringContext.bean(ISysUpgradeRecordsService.class);
    }

    private ISysBackupRecordsService sysBackupRecordsService() {
        return SpringContext.bean(ISysBackupRecordsService.class);
    }
    @Override
    public void exec(JobExecutionContext context) {
        log.info("AutoClearUpgradeRecords 定时清除升级记录、备份记录数据 {}", LocalDateTime.now());

        try {
            long threshold = LocalDateTime.now()
                    .minusDays(DAYS_TO_KEEP)
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();
            sysUpgradeRecordsService().clear(threshold);
            sysBackupRecordsService().clear(threshold);
        } catch (Exception e) {
            log.error("AutoClearUpgradeRecords 定时清除升级记录、备份记录数据失败,{}", e.getMessage());
        }
    }
}
