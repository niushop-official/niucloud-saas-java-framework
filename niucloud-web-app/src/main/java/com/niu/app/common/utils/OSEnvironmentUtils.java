package com.niu.app.common.utils;

import cn.hutool.core.util.StrUtil;

import java.util.Map;

public class OSEnvironmentUtils {

    /**
     * 获取操作系统环境变量
     *
     * @return
     */
    public static Map<String, String> getEnvs() {
        return System.getenv();
    }

    /**
     * 获取操作系统环境变量
     *
     * @param name
     * @return
     */
    public static String getEnv(String name) {
        return System.getenv(name);
    }

    /**
     * 获取操作系统Java环境变量
     *
     * @return
     */
    public static String getJavaEnv() {
        String env = getEnv("JAVA_HOME");
        if (StrUtil.isEmpty(env)) {
            return "";
        }
        return env;
    }

    /**
     * 获取操作系统Maven环境变量
     *
     * @return
     */
    public static String getMavenEnv() {
        String mavenHome = getEnv("MAVEN_HOME");
        if (StrUtil.isEmpty(mavenHome)) {
            mavenHome = getEnv("M2_HOME");
        }
        if (StrUtil.isEmpty(mavenHome)) {
            return "";
        }
        return mavenHome;
    }

    /**
     * 获取操作系统NODE环境变量
     *
     * @return
     */
    public static String getNodeEnv() {
        String env = getEnv("NODE_HOME");
        if (StrUtil.isEmpty(env)) {
            return "";
        }
        return env;
    }

    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * 是否是windows环境
     *
     * @return
     */
    public static boolean isWindowsOS() {
        String osName = getOSName();
        if (osName.contains("windows")) {
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public static boolean isLinuxOS() {
        String osName = getOSName();
        if (osName.contains("linux")) {
            return true;
        }
        return false;
    }

    /**
     * 获取maven环境变量
     *
     * @return
     */
    public static String getMvnCommand() {
        if (isWindowsOS()) {
            return getMavenEnv() + "/bin/mvn.cmd";
        } else if (isLinuxOS()) {
            return getMavenEnv() + "/bin/mvn";
        }
        return getMavenEnv();
    }

    public static void main(String[] args) {
        System.out.println("Java环境变量：" + getJavaEnv());
        System.out.println("Maven环境变量：" + getMavenEnv());
        System.out.println("Node环境变量：" + getNodeEnv());
    }

}
