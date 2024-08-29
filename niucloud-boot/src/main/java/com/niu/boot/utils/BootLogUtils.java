package com.niu.boot.utils;

import com.niu.boot.config.BootConfig;

import java.io.File;
import java.io.FileWriter;

public class BootLogUtils {

    public static boolean isLog = true;

    /**
     * @param main
     * @param object
     */
    public static void println(Object main, Object object) {
        if (isLog) {
            System.out.println(main + "," + object);
        }
    }

    /**
     * @param object
     */
    public static void println(Object object) {
        if (isLog) {
            System.out.println(object);
        }
    }

    /**
     * @param object
     */
    public static void log(Object object) {
        if (isLog) {
            System.out.println(object);
        }
    }

    /**
     * @param bootConfig
     */
    public static void resetLog(BootConfig bootConfig) {
        String logDirPath = bootConfig.getTmpHome() + "/" + bootConfig.applicationName();
        File logDirFile = new File(logDirPath);
        if (!logDirFile.exists()) {
            logDirFile.mkdirs();
        }
        File logFile = new File(logDirPath + "/start.log");
        if (logFile.isFile() && logFile.exists()) {
            File lastLogFile = new File(logDirPath + "/start.last.log");
            if (lastLogFile.exists()) {
                lastLogFile.delete();
            }
            logFile.renameTo(lastLogFile);
        }
        String logFilePath = logDirPath + "/start.log";
        record(logFilePath, "", false);
    }

    /**
     * @param bootConfig
     * @param line
     */
    public static void recordLog(BootConfig bootConfig, String line) {
        String logDirPath = bootConfig.getTmpHome() + "/" + bootConfig.applicationName();
        String logFilePath = logDirPath + "/start.log";
        try {
            File file = new File(logDirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        record(logFilePath, line + System.lineSeparator(), true);
    }

    /**
     * @param filePath
     * @param content
     * @param append
     */
    public static void record(String filePath, String content, boolean append) {
        try (FileWriter writer = new FileWriter(filePath, append)) {
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
