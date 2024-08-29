package com.niu.app.common.loader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public final class PluginBufferingApplicationStartup extends BufferingApplicationStartup {

    private static AtomicBoolean executed = new AtomicBoolean(false);

    private List<String> argList;

    public PluginBufferingApplicationStartup(int capacity) {
        super(capacity);
        this.argList = new ArrayList<>();
    }

    public PluginBufferingApplicationStartup(int capacity, List<String> argList) {
        this(capacity);
        this.argList = argList;
        if (this.argList == null) {
            this.argList = new ArrayList<>();
        }
    }

    @Override
    public StartupStep start(String name) {
        if ("spring.boot.application.environment-prepared".equals(name)) {
            if (executed.compareAndSet(false, true)) {
                boolean loadGuard = this.parseRunEnvironment();
                log.warn("spring.boot.application.environment-prepared, load-guard = " + loadGuard);
                if (loadGuard) {
                    // SystemCommandRestartTools.writeProcessPid();
                    // SystemCommandRestartTools.writeRestartScript();
                }
                ExternalJarsLoaderPlugin.load(argList, loadGuard);
            }
        } else if ("spring.context.refresh".equals(name)) {
            // 启动时钩子日志
            System.out.println("Service starting,Hook is ready.");
        }
        return super.start(name);
    }

    /**
     * @return
     */
    private boolean parseRunEnvironment() {
        String fileName = "/application.yml";
        Yaml yaml = new Yaml();
        try (InputStream inputStream = this.getClass().getResourceAsStream(fileName)) {
            Map<String, Object> yamlMap = yaml.load(inputStream);
            Map<String, Object> springMap = (Map<String, Object>) yamlMap.get("spring");
            if (springMap != null) {
                Map<String, Object> profilesMap = (Map<String, Object>) springMap.get("profiles");
                if (profilesMap != null) {
                    String active = (String) profilesMap.get("active");
                    if (!"dev".equalsIgnoreCase(active)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 第二种情况
        String runPath = PluginBufferingApplicationStartup.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (runPath.contains("webroot") && runPath.contains(".jar")) {
            return true;
        }
        return false;
    }

}
