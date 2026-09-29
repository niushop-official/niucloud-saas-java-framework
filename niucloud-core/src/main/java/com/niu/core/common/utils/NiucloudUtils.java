package com.niu.core.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.service.admin.niucloud.ICloudBuildService;
import com.niu.core.service.admin.sys.vo.SysDeveloperTokenVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import me.chanjar.weixin.common.util.crypto.SHA1;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class NiucloudUtils {

    private static String code = "";

    private static String secret = "";

    private static String developerToken = "";

    @Value("${niu-cloud.auth-code:}")
    public void setCode(String code) {
        NiucloudUtils.code = code;
    }

    @Value("${niu-cloud.auth-secret:}")
    public void setSecret(String secret) {
        NiucloudUtils.secret = secret;
    }

    public String getCode() {
        return this.code;
    }

    public String getSecret() {
        return this.secret;
    }

    public String getDeveloperToken() {
        ICoreSysConfigService sysConfigService = (ICoreSysConfigService) SpringContext.getBean(ICoreSysConfigService.class);
        SysDeveloperTokenVo developerTokenVo = sysConfigService.getDeveloperToken();
        return developerTokenVo.getToken();
    }

    public static NiucloudUtils getInstance() {
        return new NiucloudUtils();
    }

    /**
     * niucloud官网
     */
    public static class Niucloud {
        private static final String baseUrl = "https://api.niucloud.com/openapi/";
        private static final int MAX_RETRIES = 3; // 最大重试次数
        private static final long RETRY_DELAY_MS = 2000L; // 重试间隔时间 (毫秒)
        private static final Cached cache = CacheFactory.getCacheOperator();

        /**
         * 发起 GET 请求
         * @param url 请求 URL
         * @return HttpResponse 对象
         */
        public static JSONObject get(String url, Map<String, Object> query) {
            String requestUrl = baseUrl + url;
            requestUrl = HttpUtil.urlWithForm(requestUrl, query, Charset.forName("UTF-8"), false);
            return execute(HttpRequest.get(requestUrl));
        }

        /**
         * 发起 POST 请求
         * @param url 请求 URL
         * @param data 请求体
         * @return HttpResponse 对象
         */
        public static JSONObject post(String url, Object data) {
            return execute(HttpRequest.post(baseUrl + url).body(JSONUtil.parse(data).toString()));
        }

        /**
         * 发起 PUT 请求
         * @param url
         * @param data
         * @return
         */
        public static JSONObject put(String url, Object data) {
            return execute(HttpRequest.put(baseUrl + url).body(JSONUtil.parse(data).toString()));
        }

        private static HttpRequest handleRequest(HttpRequest request) {
            request.header("Referer", RequestUtils.getDomain(true));
            request.header("developer-token", NiucloudUtils.getInstance().getDeveloperToken());
            request.header("access-token", getAccessToken());
            return request;
        }

        /**
         * 执行请求并处理重试
         * @param request HttpRequest 对象
         * @return HttpResponse 对象
         */
        private static JSONObject execute(HttpRequest request) {
            int retries = 0;
            while (retries <= MAX_RETRIES) {
                try {
                    request = handleRequest(request);
                    HttpResponse response = request.execute();
                    if (response.isOk()) { // 如果响应状态码为 200
                        if (JSONUtil.isJson(response.body())) {
                            JSONObject data = JSONUtil.parseObj(response.body());
                            Integer code = data.getInt("code", 0);
                            if (code.equals(1)) {
                                return data;
                            } else if (code.equals(401)) {
                                clearAccessToken();
                                refreshAccessToken();
                                NiucloudUtils instance = NiucloudUtils.getInstance();
                                if (retries < MAX_RETRIES && !instance.code.isEmpty()) {
                                    Thread.sleep(RETRY_DELAY_MS); // 等待一段时间后重试
                                } else {
                                    throw new CommonException(data.getStr("msg", "请求异常"));
                                }
                            } else {
                                throw new CommonException(data.getStr("msg", "请求异常"));
                            }
                        }
                    } else {
                        // 处理响应错误，这里只是简单打印
                        if (retries < MAX_RETRIES) {
                            Thread.sleep(RETRY_DELAY_MS); // 等待一段时间后重试
                        } else {
                            throw new CommonException("请求异常");
                        }
                    }
                } catch (Exception e) {
                    throw new CommonException(e.getMessage());
                }
                retries++;
            }
            // 如果达到最大重试次数，返回 null 或抛出异常
            return null;
        }

        private static void refreshAccessToken() {
            Cached cache = CacheFactory.getCacheOperator();
            cache.remove("niucloud_access_token");

            try {
                NiucloudUtils instance = NiucloudUtils.getInstance();
                if (instance.code.isEmpty()) return;

                Map<String, Object> query = new HashMap<>();
                query.put("code", instance.code);
                query.put("secret", instance.secret);
                query.put("product_key", GlobalConfig.productKey);
                query.put("redirect_uri", RequestUtils.getDomain(true));
                query.put("token", createToken());

                JSONObject response = get("auth", query);
                if (response.getInt("code").equals(1)) {
                    String token = response.getByPath("data.token", String.class);
                    if (ObjectUtil.isNotEmpty(token)) {
                        setAccessToken(token);
                    }
                }
            } catch (Exception e) {
            }
        }

        public static void clearAccessToken() {
            cache.remove("niucloud_access_token");
            File accessTokenFile = new File(WebAppEnvs.get().webRootDownPublic, "access_token.txt");
            try {
                accessTokenFile.deleteOnExit();
            } catch (Exception e) {
            }
        }

        public static void setAccessToken(String accessToken) {
            cache.put("niucloud_access_token", accessToken, 7200);
        }

        public static String getAccessToken() {
            String token = (String) cache.get("niucloud_access_token");
            if (StringUtils.isEmpty(token)) {
                File accessTokenFile = new File(WebAppEnvs.get().webRootDownPublic, "access_token.txt");
                if (accessTokenFile.exists()) {
                    try {
                        token = FileUtils.readFileToString(accessTokenFile, "UTF-8");
                        setAccessToken(token);
                    } catch (Exception e) {
                    }
                }
            }
            return token;
        }

        private static String createToken() {
            NiucloudUtils instance = NiucloudUtils.getInstance();
            Map<String, Object> data = new HashMap<>();
            data.put("timestamp", DateUtils.currTime());
            data.put("code", instance.code);
            data.put("secret", instance.secret);
            data.put("nonce", RandomUtil.randomInt(0, 100));
            String token = MD5.create().digestHex(ObjectUtil.serialize(data));
            cache.put("niucloud_token", token, 7200);
            return token;
        }

        private static String getToken() {
            Cached cache = CacheFactory.getCacheOperator();
            return (String) cache.get("niucloud_token");
        }

        /**
         * 验证签名
         * @param signature
         * @param nonce
         * @param timestamp
         */
        public static void checkSignature(String signature, String timestamp, String nonce) {
            if (!signature(new String[]{ getToken(), timestamp, nonce }).equals(signature)) {
                throw new CommonException("Invalid request signature.");
            }
        }

        /**
         * 生成签名
         * @param args
         * @return
         */
        public static String signature(String[] args) {
            Arrays.sort(args);
            return SHA1.gen(String.join("", args));
        }
    }

    /**
     * niucloud云服务器
     */
    public static class Cloud {
        private String baseUrl = "http://java.oss.niucloud.com/";

        private String url;

        private HttpRequest request;

        public Cloud useThirdBuild() {
            ICloudBuildService cloudBuildService = SpringContext.bean(ICloudBuildService.class);
            Map<String, Object> config = cloudBuildService.getLocalCloudCompileConfig();
            int isOpen = (int) config.get("isOpen");
            String baseUri = (String) config.get("baseUri");
            if (isOpen == 1 && !baseUri.isEmpty()) {
                if (!baseUri.endsWith("/")) baseUri += "/";
                this.baseUrl = baseUri;
            }
            return this;
        }

        public Cloud build(String url) {
            this.url = baseUrl + url;
            this.request = new HttpRequest(this.url);
            return this;
        }

        public Cloud query(Map<String, Object> query) {
            if (query.size() > 0) {
                this.url = HttpUtil.urlWithForm(this.url, query, Charset.forName("UTF-8"), false);
                this.request.setUrl(this.url);
            }
            return this;
        }

        public Cloud header(String name, String value) {
            this.request.header(name, value);
            return this;
        }

        public Cloud header(Map<String, String> headers) {
            this.request.headerMap(headers, true);
            return this;
        }

        public Cloud method(Method method) {
            this.request.setMethod(method);
            return this;
        }

        public Cloud func(Consumer consumer) {
            consumer.doMethod(this.request);
            return this;
        }

        public String getUrl() {
            return this.request.getUrl();
        }

        @FunctionalInterface
        public interface Consumer {
            void doMethod(HttpRequest request);
        }

        public HttpResponse execute() {
            int maxRetries = 3;
            long[] backoffDelays = {100L, 200L, 400L}; // 指数退避：100ms, 200ms, 400ms

            Exception lastException = null;

            for (int i = 0; i <= maxRetries; i++) {
                try {
                    HttpResponse response = this.request.execute();
                    return response;
                } catch (Exception e) {
                    lastException = e;
                    // 如果是最后一次尝试，不再重试
                    if (i == maxRetries) {
                        break;
                    }
                    long delay = backoffDelays[i]; // 第 i 次重试的延迟时间
                    // 阻塞等待一段时间再重试
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        // 如果线程被中断，恢复中断状态并退出
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            throw new RuntimeException("HTTP请求失败: " + this.request.getUrl(), lastException);
        }
    }
}
