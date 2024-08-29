package com.niu.core.common.utils.file;

import cn.hutool.core.io.FileUtil;
import com.niu.core.common.Constants;
import com.niu.core.common.utils.system.RuntimeTools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileTools {

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
     * 删除文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
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
     * @param filePath
     * @param dataList
     */
    public static void writeList(String filePath, List<String> dataList) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        File file = new File(filePath);
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (Exception e) {
            }
        }
        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            File outputFile = new File(filePath);
            fileOutputStream = new FileOutputStream(outputFile);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, Constants.UTF8);
            if (dataList.size() <= 0) {
                outputStreamWriter.write("");
            } else {
                for (String data : dataList) {
                    outputStreamWriter.write(data);
                    outputStreamWriter.write(System.getProperty("line.separator"));
                }
            }
            outputStreamWriter.flush();
        } catch (Exception e) {
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
     * @param filePath
     * @return
     */
    public static List<String> readToList(String filePath) {
        List<String> dataList = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                file.createNewFile();
            } catch (Exception e) {
            }
        }
        if (file.isFile() && file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Constants.UTF8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    dataList.add(line);
                }
            } catch (Exception ee) {

            }
        }
        return dataList;
    }

    /**
     * @param rFile
     * @return
     */
    public static String readTempContent(String rFile) {
        try {
            String tmpHome = RuntimeTools.getTmpHome();
            String tmpFilePath = tmpHome + "/" + rFile;
            File tmpFile = new File(tmpFilePath);
            if (!tmpFile.getParentFile().exists()) {
                tmpFile.getParentFile().mkdirs();
            }
            return FileUtil.readString(tmpFile, "UTF-8");
        } catch (Exception e) {
            // ignore
        }
        return "";

    }

    /**
     * @param rFile
     * @param content
     */
    public static void writeTempContent(String rFile, String content) {
        try{
            String tmpHome = RuntimeTools.getTmpHome();
            String tmpFilePath = tmpHome + "/" + rFile;
            File tmpFile = new File(tmpFilePath);
            if (!tmpFile.getParentFile().exists()) {
                tmpFile.getParentFile().mkdirs();
            }
            write(tmpFile.getAbsolutePath(), content);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
