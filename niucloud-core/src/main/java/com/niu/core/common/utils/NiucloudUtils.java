package com.niu.core.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.exception.CommonException;
import com.niu.core.service.admin.sys.vo.SysDeveloperTokenVo;
import com.niu.core.service.core.niucloud.ICoreNiucloudConfigService;
import com.niu.core.service.core.niucloud.vo.NiucloudConfigVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class NiucloudUtils {

    private static String code = "";

    private static String secret = "";

    private String productKey = "saas_java";

    private static String developerToken = "";

    public NiucloudUtils() {
        ICoreNiucloudConfigService niucloudConfigService = (ICoreNiucloudConfigService) SpringContext.getBean(ICoreNiucloudConfigService.class);
        NiucloudConfigVo config = niucloudConfigService.getNiucloudConfig();
        code = config.getAuthCode();
        secret = config.getAuthSecret();

        ICoreSysConfigService sysConfigService = (ICoreSysConfigService) SpringContext.getBean(ICoreSysConfigService.class);
        SysDeveloperTokenVo developerTokenVo = sysConfigService.getDeveloperToken();
        developerToken = developerTokenVo.getToken();
    }

    public String getCode() {
        return this.code;
    }

    public String getSecret() {
        return this.secret;
    }

    public String getProductKey() {
        return this.productKey;
    }

    public String getDeveloperToken() {
        return this.developerToken;
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

        private static HttpRequest handleRequest(HttpRequest request) {
            request.header("Referer", RequestUtils.getDomain(true));
            request.header("developer-token", NiucloudUtils.getInstance().developerToken);

            Cached cache = CacheFactory.getCacheOperator();
            request.header("access-token", (String) ObjectUtil.defaultIfNull(cache.get("niucloud_access_token"), ""));
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
                query.put("product_key", instance.productKey);
                query.put("redirect_uri", RequestUtils.getDomain(true));

                JSONObject response = get("auth", query);
                if (response.getInt("code").equals(1)) {
                    String token = response.getByPath("data.token", String.class);
                    if (!token.isEmpty()) {
                        cache.put("niucloud_access_token", token);
                    }
                }
            } catch (Exception e) {
            }
        }

        public static void clearAccessToken() {
            Cached cache = CacheFactory.getCacheOperator();
            cache.remove("niucloud_access_token");
        }
    }

    /**
     * niucloud云服务器
     */
    public static class Cloud {
        private String baseUrl = "http://8.152.194.94:8000/";

        private String url;

        private HttpRequest request;

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
            HttpResponse response = this.request.execute();
            return response;
        }
    }
}
