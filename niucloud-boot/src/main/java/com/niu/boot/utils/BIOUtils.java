package com.niu.boot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BIOUtils {

    /**
     * @param reader
     */
    public static void close(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader = null;
        }
    }

    /**
     * @param reader
     */
    public static void close(InputStreamReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader = null;
        }
    }

    /**
     * @param process
     */
    public static void destroy(Process process) {
        if (process != null) {
            try {
                process.destroy();
                //this.process.destroyForcibly();
            } catch (Throwable target) {
                target.printStackTrace();
            }
        }
    }

    /**
     * @param process
     */
    public static void destroyForcibly(Process process) {
        if (process != null) {
            try {
               process.destroyForcibly();
            } catch (Throwable target) {
                target.printStackTrace();
            }
        }
    }

    /**
     * 获取文件的输入流
     *
     * @param location
     * @return
     */
    public static InputStream getInputStream(String location) {
        InputStream inputStream = null;
        try {
            inputStream = BIOUtils.class.getResourceAsStream(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

}
