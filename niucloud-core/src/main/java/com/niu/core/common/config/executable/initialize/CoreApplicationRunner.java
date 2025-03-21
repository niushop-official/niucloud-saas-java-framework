package com.niu.core.common.config.executable.initialize;

import cn.hutool.core.util.ClassLoaderUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.annotation.SpringModuleApplication;
import com.niu.core.common.component.context.MapperMap;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.event.EventMediatorContext;
import com.niu.core.common.component.handler.annotation.HandlerProvider;
import com.niu.core.common.component.handler.factory.HandlerProviderFactory;
import com.niu.core.common.component.initialize.annotation.Initializer;
import com.niu.core.common.component.initialize.manager.InitializeProviderManager;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.common.component.job.factory.JobProviderFactory;
import com.niu.core.common.component.loader.factory.LoaderProviderFactory;
import com.niu.core.common.component.pay.annotation.PayProvider;
import com.niu.core.common.component.pay.factory.PayProviderFactory;
import com.niu.core.common.component.sms.annotation.SmsProvider;
import com.niu.core.common.component.sms.factory.SmsProviderFactory;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.common.component.upgrade.factory.UpgradeProviderFactory;
import com.niu.core.common.component.upload.annotation.UploadProvider;
import com.niu.core.common.component.upload.factory.UploadProviderFactory;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.common.utils.language.AddonModuleBundleMessageSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * SpringBoot初始化完成后执行
 */
@Slf4j
@SuppressWarnings("unused")
@Component
class CoreApplicationRunner implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(@Nonnull ApplicationReadyEvent event) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        int delaySeconds = 30; // 延迟是为了解决【mybatis-plus-join】的初始化bug

        boolean isDev = GlobalConfig.runActive.equalsIgnoreCase("dev");
        String env = isDev ? "开发" : "生产";
        log.info("{}环境下装载模块开始.", env);

        executorService.schedule(() -> {
            Map<String, String> addonEnvsMap;
            if (isDev) {
                // 初始化已安装的插件
                addonEnvsMap = AddonResourceHelper.loadAddonEnvsMapByScan();
            } else {
                addonEnvsMap = AddonResourceHelper.loadDynamicAddon();
            }

            // 初始化已安装的插件
            log.info("addonEnvsMap = {}", addonEnvsMap);
            AddonModuleContext.init(addonEnvsMap);
            // 初始化国际化文件
            AddonModuleBundleMessageSource.init();

            // 业务资源初始化
            List<? extends Class<?>> list = scanner()
                    .findCandidateComponents("com.niu")
                    .stream()
                    .map(bd -> {
                        try {
                            return ClassLoaderUtil.loadClass(bd.getBeanClassName());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            initialize(list);

            if (!isDev) {
                // 从管道中获取命令
                PipeCommandHelper.doPipeCommand();
            }

            // 钩子输出
            System.out.println("Service started successfully.");
        }, delaySeconds, TimeUnit.SECONDS);

        executorService.shutdown();
    }

    /**
     * 业务初始化
     */
    private void initialize(List<? extends Class<?>> list) {
        // load功能初始化
        LoaderProviderFactory.init();
        // 业务事件监听及事件回调组件初始化
        EventMediatorContext.init();
        // 业务调用
        HandlerProviderFactory.init(HandlerProvider.class, list.stream().filter(o -> o.isAnnotationPresent(HandlerProvider.class)).map(o -> (Class) o).toList());
        // 上传组件初始化
        UploadProviderFactory.init(UploadProvider.class, list.stream().filter(o -> o.isAnnotationPresent(UploadProvider.class)).map(o -> (Class) o).toList());
        // 短息组件初始化
        SmsProviderFactory.init(SmsProvider.class, list.stream().filter(o -> o.isAnnotationPresent(SmsProvider.class)).map(o -> (Class) o).toList());
        // 支付组件初始化
        PayProviderFactory.init(PayProvider.class, list.stream().filter(o -> o.isAnnotationPresent(PayProvider.class)).map(o -> (Class) o).toList());
        // 初始化组件初始化
        InitializeProviderManager.exec(Initializer.class, list.stream().filter(o -> o.isAnnotationPresent(Initializer.class)).map(o -> (Class) o).toList(), SpringContext.get());
        // 测试测试
        // EventMediatorContext.test();
        // 任务调度统一实现
        JobProviderFactory.init(JobProvider.class, list.stream().filter(o -> o.isAnnotationPresent(JobProvider.class)).map(o -> (Class) o).toList());
        // 升级事件初始化
        UpgradeProviderFactory.init(UpgradeProvider.class, list.stream().filter(o -> o.isAnnotationPresent(UpgradeProvider.class)).map(o -> (Class) o).toList());
        MapperMap.init(Mapper.class, list.stream().filter(o -> o.isAnnotationPresent(Mapper.class)).map(o -> (Class) o).toList());
    }

    private ClassPathBeanDefinitionScanner scanner() {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) SpringContext.applicationContext, false);
        Stream.of(SpringModuleApplication.class, Initializer.class, HandlerProvider.class,
                        UploadProvider.class, SmsProvider.class, PayProvider.class, JobProvider.class, EventCallback.class, UpgradeProvider.class, Mapper.class)
                .forEach(o -> scanner.addIncludeFilter(new AnnotationTypeFilter(o, true, false)));
        return scanner;
    }

}
