package com.niu.core.common.config.executable.initialize;

import com.niu.core.common.utils.language.AddonModuleBundleMessageSource;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.config.dataization.AddonModuleContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SpringBoot初始化完成后执行
 */
@Component
@Order(1)
@Slf4j
public class CoreApplicationRunner implements CommandLineRunner, ApplicationRunner {

    @Override
    public void run(String... args) throws Exception {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        String basePackage = "com";
        int delaySeconds = 30; // 延迟是为了解决【mybatis-plus-join】的初始化bug
        if (GlobalConfig.runActive.equalsIgnoreCase("dev")) {
            executorService.schedule(() -> {
                try {
                    log.info("开发环境下装载模块开始.");
                    // 统一扫描注解
                    Map<Class<? extends Annotation>, List<Class>> annotationClassListMap = ResourceBeanInitializer.scanning(basePackage);
                    // 初始化已安装的插件
                    AddonModuleContext.init(AddonResourceHelper.loadAddonEnvsMapByScan());
                    // 初始化国际化文件
                    AddonModuleBundleMessageSource.init();
                    // 业务资源初始化
                    ResourceBeanInitializer.initialize(annotationClassListMap);
                    log.info("开发环境下装载模块完成.");
                    // 钩子输出
                    System.out.println("Service started successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, delaySeconds, TimeUnit.SECONDS);
            executorService.shutdown();

        } else {
            executorService.schedule(() -> {
                log.info("生产环境下装载模块开始.");
                // 初始化已安装的插件
                Map<String, String> addonEnvsMap = AddonResourceHelper.loadDynamicAddon();
                log.info("addonEnvsMap = " + addonEnvsMap);
                AddonModuleContext.init(addonEnvsMap);
                // 统一扫描注解
                Map<Class<? extends Annotation>, List<Class>> annotationClassListMap = ResourceBeanInitializer.scanning(basePackage);
                // 初始化国际化
                AddonModuleBundleMessageSource.init();
                // 业务资源初始化
                ResourceBeanInitializer.initialize(annotationClassListMap);
                // 从管道中获取命令
                PipeCommandHelper.doPipeCommand();
                log.info("生产环境下装载模块完成.");
                // 钩子输出
                System.out.println("Service started successfully.");
            }, delaySeconds, TimeUnit.SECONDS);
        }
        executorService.shutdown();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("run() start");

        log.debug("run() end");
    }

}
