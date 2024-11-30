package com.niu.core.common.config.executable.initialize;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.annotation.SpringModuleApplication;
import com.niu.core.common.component.context.MapperMap;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.common.component.upgrade.factory.UpgradeProviderFactory;
import com.niu.core.common.utils.system.ClassPathScanAnnotationManager;
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
import com.niu.core.common.component.upload.annotation.UploadProvider;
import com.niu.core.common.component.upload.factory.UploadProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public final class ResourceBeanInitializer {

    /**
     * 扫描注解
     *
     * @param basePackage
     * @return
     */
    public static Map<Class<? extends Annotation>, List<Class>> scanning(String basePackage) {
        List<Class<? extends Annotation>> scanAnnotationClassList = new ArrayList<>();
        // 插件入口注解
        scanAnnotationClassList.add(SpringModuleApplication.class);
        // 初始化插件统一提供
        scanAnnotationClassList.add(Initializer.class);
        // 业务调用统一提供
        scanAnnotationClassList.add(HandlerProvider.class);
        // 上传组件统一提供
        scanAnnotationClassList.add(UploadProvider.class);
        // 短信组件统一提供
        scanAnnotationClassList.add(SmsProvider.class);
        // 支付组件
        scanAnnotationClassList.add(PayProvider.class);
        // 任务调用统一提供
        scanAnnotationClassList.add(JobProvider.class);
        // 事件回调
        scanAnnotationClassList.add(EventCallback.class);
        // 升级事件
        scanAnnotationClassList.add(UpgradeProvider.class);
        // mapper
        scanAnnotationClassList.add(Mapper.class);
        // 统一获取系统中的注解，返回注解以及扫描到实现类列表
        Map<Class<? extends Annotation>, List<Class>> annotationClassListMap = ClassPathScanAnnotationManager.scanClassAnnotation(basePackage, scanAnnotationClassList);
        return annotationClassListMap;
    }

    /**
     * 业务初始化
     */
    public static void initialize(Map<Class<? extends Annotation>, List<Class>> annotationClassListMap) {
        // load功能初始化
        LoaderProviderFactory.init();
        // 业务调用
        HandlerProviderFactory.init(HandlerProvider.class, annotationClassListMap.get(HandlerProvider.class));
        // 上传组件初始化
        UploadProviderFactory.init(UploadProvider.class, annotationClassListMap.get(UploadProvider.class));
        // 短息组件初始化
        SmsProviderFactory.init(SmsProvider.class, annotationClassListMap.get(SmsProvider.class));
        // 支付组件初始化
        PayProviderFactory.init(PayProvider.class, annotationClassListMap.get(PayProvider.class));
        // 初始化组件初始化
        InitializeProviderManager.exec(Initializer.class, annotationClassListMap.get(Initializer.class), SpringContext.get());
        // 业务事件监听及事件回调组件初始化
        EventMediatorContext.init();
        // 测试测试
        // EventMediatorContext.test();
        // 任务调度统一实现
        JobProviderFactory.init(JobProvider.class, annotationClassListMap.get(JobProvider.class));
        // 升级事件初始化
        UpgradeProviderFactory.init(UpgradeProvider.class, annotationClassListMap.get(UpgradeProvider.class));

        MapperMap.init(Mapper.class, annotationClassListMap.get(Mapper.class));
    }

}
