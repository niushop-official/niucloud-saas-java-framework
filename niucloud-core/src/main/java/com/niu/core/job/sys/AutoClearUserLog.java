package com.niu.core.job.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.entity.sys.SysUserLog;
import com.niu.core.mapper.sys.SysUserLogMapper;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@JobProvider(key = "AutoClearUserLog", name = "定时清理用户操作日志", describe = "定时清理用户操作日志", source = "core")
public class AutoClearUserLog extends AbstractJobProvider implements IJobProvider {

    private SysUserLogMapper sysUserLogMapper() {
        return SpringContext.bean(SysUserLogMapper.class);
    }

    @Override
    public void exec(JobExecutionContext context) {
        log.info("AutoClearUserLog 定时清理用户操作日志 {}", LocalDateTime.now());

        try {
            // 计算7天前的时间戳
            long timestamp = LocalDateTime.now()
                    .minusDays(7)
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();
            sysUserLogMapper().delete(new QueryWrapper<SysUserLog>().lt("create_time", timestamp));
        } catch (Exception e) {
            log.error("AutoClearUserLog 定时清理用户操作日志,{}", e.getMessage());
        }
    }
}
