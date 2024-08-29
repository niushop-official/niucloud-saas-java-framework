package com.niu.core.common.utils.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class HttpClientUtils {

    private static CloseableHttpClient httpClient;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(20);
        cm.setDefaultMaxPerRoute(50);
        httpClient = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 发送Get请求
     */
    public static String get(String url, String param, String authorizationName, String authorization) throws Exception {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = null;
        if (param != null) {
            url = url + "?" + param;
        }
        HttpGet httpGet = new HttpGet(url);
        //设置Token
        if (authorizationName != null && authorization != null) {
            httpGet.addHeader(authorizationName, authorization);
        }
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
        httpGet.setConfig(requestConfig);
        httpGet.setConfig(requestConfig);
        httpGet.addHeader("Content-type", "application/json; charset=utf-8");
        httpGet.setHeader("Accept", "application/json");
        response = httpClient.execute(httpGet);
        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {
            sb.append(line + NL);
        }
        in.close();
        result = sb.toString();
        if (null != response) {
            response.close();
        }
        return result;
    }

    public static String post(String url, String jsonString, String authorizationName, String authorization) throws Exception {
        CloseableHttpResponse response = null;
        BufferedReader in = null;
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        //设置Token
        if (authorizationName != null && authorization != null) {
            httpPost.addHeader(authorizationName, authorization);
        }
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(30000).setSocketTimeout(30000).build();
        httpPost.setConfig(requestConfig);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(jsonString, Charset.forName("UTF-8")));
        response = httpClient.execute(httpPost);
        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {
            sb.append(line + NL);
        }
        in.close();
        result = sb.toString();
        if (null != response) {
            response.close();
        }
        return result;
    }

}
