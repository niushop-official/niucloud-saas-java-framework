package com.niu.core.job.schedule;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.service.admin.sys.ISysScheduleService;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

import java.time.Instant;
import java.time.LocalDateTime;

@Log4j2
@JobProvider(key = "AutoClearScheduleLog", name = "定时清理计划任务日志表", describe = "定时清理计划任务日志表", source = "core")
public class AutoClearScheduleLog extends AbstractJobProvider implements IJobProvider {

    private static final int SUCCESS_SAVE_DAY = 3; // 成功日志保留3天
    private static final int ERROR_SAVE_DAY = 7;   // 错误日志保留7天

    private ISysScheduleService sysScheduleService() {
        return SpringContext.bean(ISysScheduleService.class);
    }
    @Override
    public void exec(JobExecutionContext context) {
        log.info("AutoClearScheduleLog 定时清除schedule_log数据 {}", LocalDateTime.now());

        try {
            // 计算时间阈值（当前时间减去保留天数）
            long successThreshold = Instant.now().minusSeconds(SUCCESS_SAVE_DAY * 86400L).getEpochSecond();
            long errorThreshold = Instant.now().minusSeconds(ERROR_SAVE_DAY * 86400L).getEpochSecond();

            sysScheduleService().deleteScheduleLog(successThreshold, "success");
            sysScheduleService().deleteScheduleLog(errorThreshold, "error");
        } catch (Exception e) {
            log.error("AutoClearScheduleLog 定时清除schedule_log数据失败,{}", e.getMessage());
        }
    }
}
