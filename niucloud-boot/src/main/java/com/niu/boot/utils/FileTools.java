package com.niu.boot.utils;

import java.io.*;

public class FileTools {

    /**
     * 操作系统名称
     *
     * @return
     */
    public static String osName() {
        String osName = System.getProperty("os.name");
        return osName;
    }

    /**
     * 临时目录
     *
     * @return
     */
    public static String getTmpHome() {
        if (osName().contains("Windows")) {
            return System.getProperty("java.io.tmpdir");
        } else {
            String projectHome = FileTools.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if (projectHome.contains("webroot")) {
                if (osName().contains("Windows")) {
                    projectHome = projectHome.substring(1, projectHome.indexOf("webroot"));
                }
            }
            File jarFile = new File(projectHome);
            File tmpFile = new File(jarFile.getParent() + "/Temp/");
            return tmpFile.getAbsolutePath();
        }
    }

    /**
     * 不管目录是否存在直接创建
     *
     * @param filePath
     */
    public static void createDirs(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * @param filePath
     * @param suffix
     * @return
     */
    public static File[] childrenFiles(String filePath, String suffix) {
        File file = new File(filePath);
        return file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(suffix)) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * @param filePath
     * @param content
     */
    public static void write(String filePath, String content) {
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            File outputFile = new File(filePath);
            fileOutputStream = new FileOutputStream(outputFile);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, Constants.UTF8);
            outputStreamWriter.write(content);
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStreamWriter == null) {
                try {
                    outputStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream == null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param rFile
     * @param content
     */
    public static void writeTempContent(String rFile, String content) {
        try {
            String tmpHome = getTmpHome();
            String tmpFilePath = tmpHome + "/" + rFile;
            File tmpFile = new File(tmpFilePath);
            if (!tmpFile.getParentFile().exists()) {
                tmpFile.getParentFile().mkdirs();
            }
            write(tmpFile.getAbsolutePath(), content);
            System.out.println("system init lock file -> " + tmpFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
