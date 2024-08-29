package com.niu.core.job.example;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 定义触发器，触发任务
 */
public class QuartzSimpleTaskExample {

    public static class HelloJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Hello, Quartz!");
        }

    }

    public static void main(String[] args) {
        try {
            // 创建调度器工厂
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            // 定义任务细节
            JobDetail jobDetail = JobBuilder.newJob(HelloJob.class)
                    .withIdentity("myJob", "group1")
                    .usingJobData("key", "value") // 传递任务数据
                    .build();
            // 定义触发器
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("myTrigger", "group1")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(10) // 每10秒执行一次
                            .repeatForever())
                    .build();
            // 将任务细节和触发器注册到调度器
            scheduler.scheduleJob(jobDetail, trigger);
            // 启动调度器
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
