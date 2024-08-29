package com.niu.boot.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.*;

public class JsonLoadUtils {

    /**
     * 从文件中读取，返回：JSON字符串，若文件不存在等情况这返回：""
     *
     * @param file
     * @return
     */
    public static String loadJsonString(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Constants.UTF8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 从文件中读取JSON文件，返回JSON对象，若不存在则返回空JSON对象
     *
     * @param filePath
     * @return
     */
    public static JSONObject readFileToJSONObject(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return new JSONObject();
        }
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
        JSONArray jsonArray = null;
        try {
            jsonArray = JSONUtil.parseArray(jsonString);
        } catch (Exception e) {
            // ignore
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }

}
