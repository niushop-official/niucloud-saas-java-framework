package com.niu.core.common.config.executable.initialize;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.install.IInstallSystemService;
import com.niu.core.service.core.addon.AddonInstallJavaTools;
import com.niu.core.service.core.addon.AddonInstallTools;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.app.tools.SQLScriptRunnerTools;
import com.niu.core.service.core.schedule.ICoreScheduleService;
import com.niu.core.service.core.sys.ICoreMenuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Spring Context声明周期，刷新Context后执行
 */
@Component
@Slf4j
public class CoreSpringContextListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Resource
    private SiteMapper siteMapper;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("当前应用运行环境：" + GlobalConfig.runActive);
        log.info("当前应用运行环境工作目录：" + WebAppEnvs.get());
        if (this.isInit()) {
            // 初始话菜单
            this.initMenu();
            // 初始话计划任务
            this.initSchedule();
            // 安裝插件
            if (this.isInstallAddon()) this.installAddon();
            // 创建完成
            this.setCreated();
            Site site = new Site();
            site.setIsinit(1);
            siteMapper.update(site, new QueryWrapper<Site>().eq("site_id", 0));
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
        int delaySeconds = 30;  // 延迟是为了解决【mybatis-plus-join】的初始化bug，避免mybatis调用错误
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
     * 初始化计划任务
     */
    private void initSchedule() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        int delaySeconds = 30;  // 延迟是为了解决【mybatis-plus-join】的初始化bug，避免mybatis调用错误
        executorService.schedule(() -> {
            try {
                ICoreScheduleService coreScheduleService = SpringContext.bean(ICoreScheduleService.class);
                coreScheduleService.installSystemSchedule();
                log.warn("初始化计划任务已创建.");
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
        Site site = siteMapper.selectById(0);
        if(site.getIsinit() == 0)
        {
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

    /**
     * 安裝插件
     */
    private void installAddon() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        int delaySeconds = 30;  // 延迟是为了解决【mybatis-plus-join】的初始化bug，避免mybatis调用错误
        executorService.schedule(() -> {
            log.info("开始安装插件");
            try {
                // 获取本地所有的插件
                List<File> localAddons = Files.list(Paths.get(WebAppEnvs.get().webRootDownAddon))
                        .map(path -> path.toFile())
                        .filter(file -> file.isDirectory())
                        .collect(Collectors.toList());

                System.out.println("localAddons");
                System.out.println(localAddons);

                AddonInstallTools addonInstallTools = SpringContext.bean(AddonInstallTools.class);
                ICoreMenuService coreMenuService = SpringContext.bean(ICoreMenuService.class);
                ICoreScheduleService coreScheduleService = SpringContext.bean(ICoreScheduleService.class);
                ICoreAddonService coreAddonService = SpringContext.bean(ICoreAddonService.class);

                for (File file : localAddons) {
                    log.info("安装插件" + new File(file, "info.json").getAbsolutePath());
                    if (new File(file, "info.json").exists()) {
                        JSONObject info = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(file, "info.json")));
                        String addon = info.getStr("key");
                        // 合并文件
                        addonInstallTools.installDir(addon);
                        // 合并依赖
                        addonInstallTools.installDepend(addon);
                        // 合并uniapp文件
                        addonInstallTools.installUniapp(addon);
                        // 安装菜单
                        coreMenuService.installAddonMenu(addon);
                        // 安装数据库
                        File installSql = new File(file, "sql/install.sql");
                        if (installSql.exists()) {
                            String sqlContent = FileUtils.readFileToString(installSql, "UTF-8");
                            sqlContent = sqlContent.replace("{{prefix}}", GlobalConfig.tablePrefix);
                            SQLScriptRunnerTools.execScript(sqlContent);
                        }
                        coreScheduleService.installAddonSchedule(addon);

                        coreAddonService.set(JSONUtil.toBean(info, Addon.class));

                        AddonInstallJavaTools.addAddonJson(addon);
                    }
                }
            } catch (Exception e) {
                log.warn("插件安装失败");
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * 是否安装插件
     *
     * @return
     */
    private boolean isInstallAddon() {
        try (RandomAccessFile raf = new RandomAccessFile("install_addon.dat", "rw");
            FileChannel channel = raf.getChannel()) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 100);
            // 读取数据
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);
            String message = new String(data).trim();

            if (message.equals("install-addon")) {
                clearBuffer(buffer);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void clearBuffer(MappedByteBuffer buffer) {
        buffer.rewind(); // 将位置重置为 0
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) 0);
        }
    }
}
