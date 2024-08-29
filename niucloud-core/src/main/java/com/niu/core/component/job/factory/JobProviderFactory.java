package com.niu.core.component.job.factory;

import cn.hutool.core.util.StrUtil;
import com.niu.core.component.job.IJobProvider;
import com.niu.core.component.job.annotation.JobProvider;
import com.niu.core.component.job.model.JobInfo;
import com.niu.core.component.job.quartz.QuartzJobManager;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public final class JobProviderFactory {

    // 对应关系模型容器
    public static Map<String, Class> jobProviderClassMap = new HashMap<>();
    public static Map<String, Set<String>> jobProviderNameMap = new HashMap<>();

    /**
     * 初始化执行
     *
     * @param annotationClass
     * @param annotationImplClassList
     */
    public static synchronized void init(Class<? extends JobProvider> annotationClass, List<Class> annotationImplClassList) {
        if (annotationClass == null || annotationImplClassList == null || annotationImplClassList.size() <= 0) {
            return;
        }
        for (Class annotationImplClass : annotationImplClassList) {
            JobProvider jobProvider = (JobProvider) annotationImplClass.getAnnotation(annotationClass);
            if (jobProvider != null) {
                String source = jobProvider.source();
                String key = jobProvider.key();
                if (StrUtil.isNotEmpty(key) && StrUtil.isNotEmpty(source)) {
                    try {
                        // Job名称和Job类
                        jobProviderClassMap.put(key, annotationImplClass);
                        // 插件名称对应Job名称
                        Set<String> nameSet = jobProviderNameMap.get(source);
                        if (nameSet == null) {
                            nameSet = new HashSet<>();
                        }
                        nameSet.add(key);
                        jobProviderNameMap.put(source, nameSet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        QuartzJobManager.init();
        log.info("init() 初始化已完成.");
        log.info("jobProviderClassMap = " + jobProviderClassMap);

    }

    /**
     * @param key
     * @return
     */
    static Class<? extends IJobProvider> getJobProvider(String key) {
        return jobProviderClassMap.get(key);
    }

    /**
     * @return
     */
    static Map<String, Class> getJobProviderClassMap() {
        return jobProviderClassMap;
    }

    /**
     * @return
     */
    static Map<String, Set<String>> getJobProviderNameMap() {
        return jobProviderNameMap;
    }

    /**
     * @param key
     * @return
     */
    public static Class<? extends IJobProvider> getJob(String key) {
        return jobProviderClassMap.get(key);
    }


    /**
     * @return
     */
    public static Set<String> jobKeys() {
        return jobProviderClassMap.keySet();
    }

    /**
     * @param key
     * @return
     */
    public static JobInfo getJobInfo(String key) {
        JobInfo jobInfo = new JobInfo();
        Class<?> jobProviderClass = jobProviderClassMap.get(key);
        if (jobProviderClass == null) {
            return null;
        }
        JobProvider jobProvider = jobProviderClass.getAnnotation(JobProvider.class);
        if (jobProvider != null) {
            jobInfo.setKey(key);
            jobInfo.setName(jobProvider.name());
            jobInfo.setDescribe(jobProvider.describe());
            jobInfo.setSource(jobProvider.source());
            jobInfo.setJobClass(jobProviderClass.getName());
        } else {
            return null;
        }
        return jobInfo;
    }

}
