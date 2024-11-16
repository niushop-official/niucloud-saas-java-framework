package com.niu.core.common.utils.json;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.config.dataization.AddonModuleContext;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JsonLoadUtils {

    /**
     * 从文件中读取，返回：JSON字符串，若文件不存在等情况这返回：""
     *
     * @param file
     * @return
     */
    public static String loadJsonString(File file) {
        if (!file.exists()) {
            return "";
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 从文件中读取JSON文件，返回JSON对象，若不存在则返回空JSON对象
     *
     * @param filePath
     * @return
     */
    public static JSONObject readFileToJSONObject(String filePath) {
        File file = new File(filePath);
        String jsonString = JsonLoadUtils.loadJsonString(file.getAbsoluteFile());
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONUtil.parseObj(jsonString);
        } catch (Exception e) {
            // ignore
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }

    /**
     * 从文件中读取JSON文件，返回JSON数组，若不存在则返回空JSON数组
     *
     * @param filePath
     * @return
     */
    public static JSONArray readFileToJSONArray(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return new JSONArray();
        }
        String jsonString = JsonLoadUtils.loadJsonString(file.getAbsoluteFile());
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = JSONUtil.parseArray(jsonString);
        } catch (Exception e) {
            // ignore
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }

    /**
     * 必须以根开头，eg: /addon.json
     *
     * @param classpath
     * @return
     */
    public static String loadJsonToString(String classpath) {
        InputStream inputStream = getInputStream(classpath);
        if (inputStream != null) {
            try {
                return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
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
            inputStream = JsonLoadUtils.class.getResourceAsStream(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * @param location
     * @return
     * @throws IOException
     */
    public static String readFileFromClasspath(String location) throws IOException {
        return readFileFromClasspath(AddonModuleContext.getSystemResourceLoader(), location);
    }

    /**
     * @param resourceLoader
     * @param location
     * @return
     * @throws IOException
     */
    public static String readFileFromClasspath(ResourceLoader resourceLoader, String location) throws IOException {
        InputStream inputStream = resourceLoader.getResource("classpath:" + location).getInputStream();
        try {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @return
     * @throws IOException
     */
    public static String readFileFromFile(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static JSONObject readJsonObjectFromJar(String webJarPath) {
        String json = "";
        try {
            File jarFile = new File(webJarPath);
            String jarProtocol = "jar:file:///" + jarFile.getAbsoluteFile() + "!info.json";
            jarProtocol = jarProtocol.replace("\\", "/");
            System.out.println(jarProtocol);
            URL url = URI.create(jarProtocol).toURL();
            InputStream openInputStream = url.openStream();
            json = IOUtils.toString(openInputStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StrUtil.isEmpty(json)) {
            return null;
        }
        return JSONUtil.parseObj(json);
    }

    /**
     * @param webJarPath
     * @param entryName
     * @return
     */
    public static JSONObject readJsonObjectFromJarInner(String webJarPath, String entryName) {
        String json = "";
        InputStream inputStream = null;
        try {
            JarFile jarFile = new JarFile(webJarPath);
            JarEntry entry = jarFile.getJarEntry(entryName);
            if (entry != null) {
                inputStream = jarFile.getInputStream(entry);
                json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (StrUtil.isEmpty(json)) {
            return null;
        }
        return JSONUtil.parseObj(json);
    }

    public static void main(String[] args) {
        String webJarPath = "D:\\MyData\\NiuCloud\\NiuShop.20240703\\niucloud-admin-java\\webroot\\jar\\addon\\shop.jar";
        JSONObject jsonObject = readJsonObjectFromJarInner(webJarPath, "info.json");
        System.out.println(jsonObject);

    }
}
