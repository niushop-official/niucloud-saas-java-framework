package com.niu.boot.utils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PipeNameUtils {

    /**
     * @param pipePath
     * @param encoding
     * @return
     */
    public static String readContext(String pipePath, String pipeName, String encoding) {
        StringBuilder stringBuilder = new StringBuilder();
        Path pipe = Paths.get(pipePath);
        File pipeDir = pipe.toFile();
        if (!pipeDir.exists()) {
            try {
                pipeDir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File pipeFile = new File(pipeDir.getAbsolutePath() + "/" + pipeName);
        if (!pipeFile.exists()) {
            try {
                pipeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(pipeFile);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, encoding);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (Exception ee) {
                // ignore
            }
        }
        return stringBuilder.toString();
    }

    /**
     * @param pipePath
     * @param pipeName
     * @param fileEncoding
     * @param command
     */
    public static void writeContext(String pipePath, String pipeName, String fileEncoding, String command) {
        Path pipe = Paths.get(pipePath);
        File pipeDir = pipe.toFile();
        if (!pipeDir.exists()) {
            try {
                pipeDir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File pipeFile = new File(pipeDir.getAbsolutePath() + "/" + pipeName);
        if (!pipeFile.exists()) {
            try {
                pipeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(pipeFile);
             OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, fileEncoding)) {
            writer.write(command);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
