package com.niu.core.common.utils.http;

import com.alibaba.fastjson2.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpConnectionPoolManagerDemo {

    public static void main(String[] args) {
        String host = "www.sxmfyy.com";
        int port = 443;
        HttpConnectionPoolManager poolManager = new HttpConnectionPoolManager(host, port);
        // 调用URL
        String url = "https://www.sxmfyy.com/api/auth/login";
        // 请求参数
        Map<String, Object> requestJsonMap = new HashMap<>();
        requestJsonMap.put("username", "admin");
        requestJsonMap.put("password", "123456");
        String requestJson = JSON.toJSONString(requestJsonMap);
        //
        CloseableHttpClient httpClient = poolManager.getDefaultCustomClient();
        HttpPost httpPost = new HttpPost(url);
        // httpPost.addHeader("Authorization", "");
        StringEntity requestEntity = new StringEntity(requestJson, "UTF-8");
        requestEntity.setContentType("application/json");
        httpPost.setEntity(requestEntity);
        CloseableHttpResponse response = null;
        String responseJson = "";
        try {
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            System.out.println("statusLine=>" + response.getStatusLine());
            responseJson = EntityUtils.toString(responseEntity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("responseJson=>" + responseJson);
        // 关闭
        poolManager.shutdownConnectionManager();
    }

}
