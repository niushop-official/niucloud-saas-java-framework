package com.niu.boot.config;

import cn.hutool.core.util.StrUtil;
import com.niu.boot.utils.BootLogUtils;
import com.niu.boot.utils.Constants;
import com.niu.boot.utils.FileTools;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class BootConfig {

    private Map<String, Object> yamlMap;

    private Map<String, Map<String, Object>> bootMap;

    private String runPath;

    /**
     * @param args
     * @param classpath
     */
    public BootConfig(String[] args, String classpath) {
        if (classpath == null) {
            classpath = "/boot.yml";
        }
        if (!classpath.startsWith("/")) {
            classpath = "/" + classpath;
        }
        Yaml yaml = new Yaml();
        try (InputStream inputStream = this.getClass().getResourceAsStream(classpath)) {
            this.yamlMap = yaml.load(inputStream);
            this.bootMap = (Map<String, Map<String, Object>>) yamlMap.get("boot");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("配置文件错误: ", e);
        }
        BootLogUtils.isLog = this.printLog();
        // this.checkAndLog();
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
    }

    /**
     * 运行目录
     *
     * @return
     */
    public String getRunPath() {
        this.runPath = BootConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (runPath.toLowerCase().endsWith("/webroot/jar/web-app-boot-exec.jar")) {
            //
        }
        return this.runPath;
    }

    /**
     * 运行环境
     *
     * @return
     */
    public String environment() {
        String runPath = BootConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (runPath.toLowerCase().contains("/webroot/jar/web-app-boot-exec.jar")) {
            return "prod";
        }
        return "dev";
    }

    /**
     * 是否打印日志
     *
     * @return
     */
    public Boolean printLog() {
        Map<String, Object> applicationMap = this.bootMap.get("application");
        return (Boolean) applicationMap.get("print-Log");
    }

    /**
     * @return
     */
    public Integer timeToIdleSeconds() {
        Map<String, Object> monitorMap = this.bootMap.get("monitor");
        return (Integer) monitorMap.get("time-to-idle-seconds");
    }

    /**
     * 应用名称
     *
     * @return
     */
    public String applicationName() {
        Map<String, Object> managerMap = this.bootMap.get("manager");
        return (String) managerMap.get("app-name");
    }

    /**
     * @return
     */
    public String jarName() {
        Map<String, Object> managerMap = this.bootMap.get("manager");
        return (String) managerMap.get("jar-name");
    }

    /**
     * @return
     */
    public String jarPath() {
        Map<String, Object> managerMap = this.bootMap.get("manager");
        return (String) managerMap.get("jar-path");
    }

    /**
     * @return
     */
    public Integer failedRestartCount() {
        Map<String, Object> managerMap = this.bootMap.get("manager");
        return (Integer) managerMap.get("failed-restart-count");
    }

    /**
     * @return
     */
    public String serviceStartingHookFlag() {
        Map<String, Object> managerMap = this.bootMap.get("manager");
        return (String) managerMap.get("service-starting-hook-flag");
    }

    /**
     * @return
     */
    public String serviceStartedSuccessfullyFlag() {
        Map<String, Object> managerMap = this.bootMap.get("manager");
        return (String) managerMap.get("service-started-successfully-flag");
    }

    /**
     * 打印启动参数
     */
    public void printArguments() {
        BootLogUtils.log("------------------------------------------------------------------");
        BootLogUtils.log("application.environment -> " + environment());
        BootLogUtils.log("application.print-Log -> " + printLog());
        BootLogUtils.log("manager.app-name -> " + applicationName());
        BootLogUtils.log("manager.jar-path -> " + this.getJarDir());
        BootLogUtils.log("manager.jar-name -> " + jarName());
        BootLogUtils.log("manager.root-path -> " + this.getRootPath());
//        BootLogUtils.log("monitor.time-to-idle-seconds -> " + timeToIdleSeconds());
//        BootLogUtils.log("manager.failed-restart-count -> " + failedRestartCount());
//        BootLogUtils.log("manager.service-starting-hook-flag -> " + serviceStartingHookFlag());
//        BootLogUtils.log("boot.manager.service-started-successfully-flag -> " + serviceStartedSuccessfullyFlag());
        BootLogUtils.log("------------------------------------------------------------------");
    }

    /**
     * @return
     */
    public String getJavaVersion() {
        return System.getProperty("java.version");
    }

    /**
     * @return
     */
    public String getWorkDir() {
        return getRunPath(BootConfig.class);
    }

    /**
     * @return
     */
    public String getApplicationYml() {
        return this.getJarDir() + "application.yml";
    }

    /**
     * @return
     */
    public String getApplicationYmlBoot() {
        return this.getJarDir() + "application-" + this.environment() + ".yml";
    }

    /**
     * 获取运行时Home的路径
     *
     * @param clazz
     * @return
     */
    private static String getRunPath(Class clazz) {
        String encoding = System.getProperty("sun.jnu.encoding");
        if (encoding == null || "".equals(encoding)) {
            encoding = "UTF-8";
        }
        String runPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodeRunPath = null;
        try {
            decodeRunPath = URLDecoder.decode(runPath, encoding);
        } catch (Exception e) {
        }
        String lowerCaseRunPath = decodeRunPath.toLowerCase();
        if (lowerCaseRunPath.contains(".jar")) {
            int jarIndex = lowerCaseRunPath.indexOf(".jar");
            decodeRunPath = decodeRunPath.substring(0, jarIndex);
        }
        return new File(decodeRunPath).getParent();
    }

    /**
     * @return
     */
    public String getJavaExec() {
        if (false) {
            String javaHome = System.getenv("JAVA_HOME");
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("windows")) {
                return javaHome + "/bin/java.exe";
            }
            return javaHome + "/bin/java";
        }
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return "java.exe";
        }
        return "java";
    }

    /**
     * @return
     */
    public String getJarDir() {
        if ("boot".equals(this.environment())) {
            return this.getWorkDir() + "/";
        } else if ("dev".equals(this.environment())) {
            String runPath = BootConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            runPath = runPath.substring(1, runPath.indexOf("niucloud-admin-java") + "niucloud-admin-java".length());
            runPath = runPath + "/webroot/jar/";
            return runPath;
        } else {
            return this.getWorkDir() + "/";
        }
    }

    public String getRootPath() {
        String runPath = getRunPath(BootConfig.class);
        if (runPath.startsWith("file:\\")) {
            runPath = runPath.substring(6, runPath.length());
        }
        if (runPath.startsWith("file:")) {
            runPath = runPath.substring(5, runPath.length());
        }
        String webroot = runPath;
        if (webroot.contains("webroot")) {
            webroot = webroot.substring(0, webroot.indexOf("webroot"));
        }
        if (webroot.startsWith("nested:/")) {
            webroot = webroot.substring(7);
        }
        return webroot;
    }

    /**
     * @return
     */
    public String getJarPath() {
        File file = new File(this.getJarDir() + this.jarName());
        return file.getAbsolutePath();
    }

    /**
     * @param applicationName
     * @return
     */
    public String getJvmOpts(String applicationName) {
        String jvmArg = "-Dname=" + applicationName + " -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -XX:+UseZGC";
        return jvmArg;
    }

    /**
     * @return
     */
    public String getPrintEncode() {
        if (FileTools.osName().contains("Windows")) {
            return Constants.GBK;
        }
        return Constants.UTF8;
    }

    /**
     * @return
     */
    public String getTmpHome() {
        return FileTools.getTmpHome();
    }

    /**
     * @return
     */
    public String getEncoding() {
        return Constants.UTF8;
    }


    /**
     * @return
     */
    public String getAppEnvPath() {
        File file = new File(this.getJarDir() + "/application-prod.yml");
        return file.getAbsolutePath();
    }


}
