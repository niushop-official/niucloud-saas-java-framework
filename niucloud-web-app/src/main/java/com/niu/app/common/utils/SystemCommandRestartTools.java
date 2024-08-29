package com.niu.app.common.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

public final class SystemCommandRestartTools {

    /**
     * 获取运行时 PID
     *
     * @return
     */
    public static long processPid() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long pid = Long.parseLong(runtimeMXBean.getName().split("@")[0]);
        return pid;
    }

    /**
     * 写入PID
     */
    public static void writeProcessPid() {
        String userDir = System.getProperty("user.dir") + "/pid";
        writeFile(userDir, processPid() + "");
    }

    /**
     * 写入PID
     */
    public static void writeRestartScript() {
        String userDir = System.getProperty("user.dir") + "/";
        generateRestartScript(userDir);
    }

    /**
     * 生成脚本
     *
     * @param home
     */
    public static void generateRestartScript(String home) {
        if (isWindowsOS()) {
            generateWindowsRestartScript(home, home + "/restart.bat");
        } else if (isLinuxOS()) {
            generateLinuxRestartScript(home, home + "/restart.sh");
        }
    }

    /**
     * @param home
     * @param fileName
     */
    private static void generateLinuxRestartScript(String home, String fileName) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append("");
            stringBuilder.append("");
            stringBuilder.append("");
            stringBuilder.append("");
            writeFile(fileName, stringBuilder.toString());
            Path scriptPath = Paths.get(fileName);
            Files.setPosixFilePermissions(scriptPath, PosixFilePermissions.fromString("rwxr--r--"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param home
     * @param fileName
     */
    private static void generateWindowsRestartScript(String home, String fileName) {
        try {
            String line = System.getProperty("line.separator");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("@echo off").append(line);
            stringBuilder.append(line);
            stringBuilder.append("setlocal").append(line);
            stringBuilder.append("set \"pid=\"").append(line);
            stringBuilder.append("for /f \"usebackq delims=\" %%a in (\"pid\") do (").append(line);
            stringBuilder.append("    set \"pid=%%a\"").append(line);
            stringBuilder.append(")").append(line);
            stringBuilder.append("echo %pid%").append(line);
            stringBuilder.append("taskkill -f -pid %pid%").append(line);
            stringBuilder.append("echo %pid%").append(line);
            stringBuilder.append("timeout /T 3 /NOBREAK").append(line);
            stringBuilder.append("java -jar " + home + "web-app-exec.jar").append(line);
            stringBuilder.append("endlocal").append(line);
            writeFile(fileName, stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileName
     * @param text
     */
    public static void writeFile(String fileName, String text) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.write(text);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
     * @return
     */
    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

}
