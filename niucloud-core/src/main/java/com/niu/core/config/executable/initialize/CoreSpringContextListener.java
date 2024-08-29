package com.niu.core.config.executable.initialize;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.language.AddonModuleBundleMessageSource;
import com.niu.core.component.context.SpringContext;
import com.niu.core.component.context.WebAppEnvs;
import com.niu.core.config.GlobalConfig;
import com.niu.core.config.dataization.AddonModuleContext;
import com.niu.core.service.admin.install.IInstallSystemService;
import com.niu.core.service.core.app.tools.SQLScriptRunnerTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Spring Context声明周期，刷新Context后执行
 */
@Component
@Slf4j
public class CoreSpringContextListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("当前应用运行环境：" + GlobalConfig.runActive);
        log.info("当前应用运行环境工作目录：" + WebAppEnvs.get());
        System.setProperty("log.path", WebAppEnvs.get().webRootDownJar + "/logs/");
        if (this.isInit()) {
            // 第一次安装时初始化创建
            this.initSqlSchema();
            this.initSqlData();
            // 初始话菜单
            this.initMenu();
            // 创建完成
            this.setCreated();
        }
        // 根据管道命令初始化执行
        PipeCommandHelper.initAppBasicByPipeCommand();
    }

    /**
     * 初始化SQL数据
     */
    private void initSqlData() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sql/niucloud_data.sql");
        try {
            String sqlContent = IOUtils.toString(inputStream, "UTF-8");
            String tablePrefix = GlobalConfig.tablePrefix;
            if (!GlobalConfig.tablePrefix.endsWith("_")) {
                tablePrefix = tablePrefix + "_";
            }
            if (StrUtil.isNotEmpty(sqlContent)) {
                sqlContent = sqlContent.replaceAll("\\{table_prefix\\}", tablePrefix);
                SQLScriptRunnerTools.execScript(sqlContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化SQL脚本
     */
    private void initSqlSchema() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sql/niucloud_schema.sql");
        try {
            String sqlContent = IOUtils.toString(inputStream, "UTF-8");
            String tablePrefix = GlobalConfig.tablePrefix;
            if (!GlobalConfig.tablePrefix.endsWith("_")) {
                tablePrefix = tablePrefix + "_";
            }
            if (StrUtil.isNotEmpty(sqlContent)) {
                sqlContent = sqlContent.replaceAll("\\{table_prefix\\}", tablePrefix);
                SQLScriptRunnerTools.execScript(sqlContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        int delaySeconds = 30;
        executorService.schedule(() -> {
            try {
                IInstallSystemService installSystemService = SpringContext.bean(IInstallSystemService.class);
                installSystemService.install();
                log.warn("菜单已创建.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delaySeconds, TimeUnit.SECONDS);
        executorService.shutdown();
    }

    /**
     * @return
     */
    private boolean isInit() {
        String initedContent = FileTools.readTempContent(GlobalConfig.applicationName + "/inited");
        if ("init".equals(initedContent)) {
            return true;
        }
        return false;
    }

    /**
     * 设置已创建
     */
    private void setCreated() {
        FileTools.writeTempContent(GlobalConfig.applicationName + "/inited", "created");
    }


}
