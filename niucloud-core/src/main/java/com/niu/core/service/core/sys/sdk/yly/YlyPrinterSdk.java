package com.niu.core.service.core.sys.sdk.yly;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yly.print_sdk_library.UtilUrl;
import com.yly.print_sdk_library.Utils;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 官方给的SDK只支持单client连接，弃用上层请求部分封装
 * 本类拷贝sdk{@link com.yly.print_sdk_library.RequestMethod}
 * 相比起官方提供的，优化了性能，并且优化了使用体验
 *
 * @link <a href="https://gitee.com/ojh/YLYSDK_JAVA"></a>
 */
@Slf4j
@Getter
public class YlyPrinterSdk {
    private static final String example = "{\"error\":\"20\",\"error_description\":\"success\",\"body\":\"未完成初始化\"}";
    public final String clientId;
    public final String clientSecret;

    private String token;

    public YlyPrinterSdk(String clientId, String clientSecret) {
        this(clientId, clientSecret, null);
    }

    public YlyPrinterSdk(String clientId, String clientSecret, String token) {
        if (ObjectUtil.hasEmpty(clientId, clientSecret)) {
            throw new RuntimeException("打印机连接失败，请检查参数");
        }

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.token = token;
    }

    public AccessTokenBody getAccessToken() {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("client_id", clientId);
        paramMap.put("grant_type", "client_credentials");
        paramMap.put("sign", Utils.getMD5Str(signMD5));
        paramMap.put("scope", "all");
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        String body = HttpRequest.sendPost(UtilUrl.getFreeType(), paramMap);

        AccessTokenBody tokenBody = JSONUtil.toBean(body, AccessTokenBody.class);
        this.token = tokenBody.getAccessToken();
        return tokenBody;
    }

    public String getRefreshAccessToken(String refresh_token) {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("refresh_token", refresh_token);
        paramMap.put("grant_type", "refresh_token");
        paramMap.put("scope", "all");
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getFreeType(), paramMap);
    }

    public String addPrinter(String machine_code, String msign) {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("machine_code", machine_code);
        paramMap.put("msign", msign);
        paramMap.put("access_token", token);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getAddPrinter(), paramMap);
    }

    public String addPrinter(String machine_code, String msign, String phone, String print_name) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("machine_code", machine_code);
        paramMap.put("msign", msign);
        paramMap.put("access_token", token);
        paramMap.put("phone", phone);
        paramMap.put("print_name", print_name);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getAddPrinter(), paramMap);
    }

    public String scanCodeModel(String machine_code, String qr_key) {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("machine_code", machine_code);
        paramMap.put("qr_key", qr_key);
        paramMap.put("scope", "all");
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getScanCodeModel(), paramMap);
    }

    public String scanCodeModel_msign(String machine_code, String msign) {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("machine_code", machine_code);
        paramMap.put("msign", msign);
        paramMap.put("scope", "all");
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getScanCodeModel(), paramMap);
    }

    public String printIndex(String machine_code, String content, String origin_id) {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("content", content);
        paramMap.put("origin_id", origin_id);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintIndex(), paramMap);
    }

    public String picturePrintIndex(String machine_code, String picture_url, String origin_id) {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        try {
            paramMap.put("picture_url", URLEncoder.encode(picture_url, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        paramMap.put("origin_id", origin_id);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPicturePrintIndex(), paramMap);
    }

    public String expressPrintIndex(String machine_code, String content, String origin_id) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("content", content);
        paramMap.put("origin_id", origin_id);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getExpressPrintIndex(), paramMap);
    }

    public String printerSetVoice(String machine_code, String content, String is_file, String aid, String origin_id) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("content", content);
        paramMap.put("is_file", is_file);
        paramMap.put("aid", aid);
        paramMap.put("origin_id", origin_id);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrinterSetVoice(), paramMap);
    }

    public String printerDeleteVoice(String machine_code, String aid, String origin_id) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("aid", aid);
        paramMap.put("origin_id", origin_id);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrinterDeleteVoice(), paramMap);
    }

    public String printerDeletePrinter(String machine_code) {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrinterDeletePrinter(), paramMap);
    }

    public String printMenuAddPrintMenu(String machine_code, String content) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("content", content);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintMenuAddPrintMenu(), paramMap);
    }

    public String printShutdownRestart(String machine_code, String response_type) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("response_type", response_type);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintShutdownRestart(), paramMap);
    }

    public String printSetSound(String machine_code, String response_type, String voice) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("response_type", response_type);
        paramMap.put("voice", voice);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintSetSound(), paramMap);
    }

    public String printPrintInfo(String machine_code) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintPrintInfo(), paramMap);
    }

    public String printGetVersion(String machine_code) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintGetVersion(), paramMap);
    }

    public String printCancelAll(String machine_code) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintCancelAll(), paramMap);
    }

    public String printCancelOne(String machine_code, String order_id) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("order_id", order_id);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintCancelOne(), paramMap);
    }

    public String printSetIcon(String machine_code, String img_url) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("img_url", img_url);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintSetIcon(), paramMap);
    }

    public String printDeleteIcon(String machine_code) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintDeleteIcon(), paramMap);
    }

    public String printBtnPrint(String machine_code, String response_type) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("response_type", response_type);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintBtnPrint(), paramMap);
    }

    public String printGetOrder(String machine_code, String response_type) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("response_type", response_type);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrintGetOrder(), paramMap);
    }

    public String oauthSetPushUrl(String machine_code, String cmd, String url, String status) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("cmd", cmd);
        paramMap.put("url", url);
        paramMap.put("status", status);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getOauthSetPushUrl(), paramMap);
    }

    public String printerGetOrderStatus(String machine_code, String order_id) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("order_id", order_id);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrinterGetOrderStatus(), paramMap);
    }

    public String printerGetOrderPagingList(String machine_code, String page_index, String page_size) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("page_index", page_index);
        paramMap.put("page_size", page_size);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrinterGetOrderPagingList(), paramMap);
    }

    public String printerGetPrintStatus(String machine_code) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        return HttpRequest.sendPost(UtilUrl.getPrinterGetPrintStatus(), paramMap);
    }

    public String printerReprintorder(String machine_code, String order_id) throws Exception {

        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        paramMap.put("order_id", order_id);
        return HttpRequest.sendPost(UtilUrl.getPrinterReprintorder(), paramMap);
    }

    public String expressprintCancel(String machine_code, String content) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        paramMap.put("content", content);
        return HttpRequest.sendPost(UtilUrl.getExpressprintCancel(), paramMap);
    }

    public String printerSetkeywords(String machine_code, String keys, String type, String content) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        paramMap.put("keys", keys);
        paramMap.put("type", type);
        paramMap.put("content", content);
        return HttpRequest.sendPost(UtilUrl.getPrinterSetkeywords(), paramMap);
    }

    public String printerPushswitch(String machine_code, String status, int mode) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        paramMap.put("status", status);
        paramMap.put("mode", Integer.toString(mode));
        return HttpRequest.sendPost(UtilUrl.getPrinterPushswitch(), paramMap);
    }

    public String printerPushswitch(String machine_code, String status) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        paramMap.put("status", status);
        paramMap.put("mode", "1");
        return HttpRequest.sendPost(UtilUrl.getPrinterPushswitch(), paramMap);
    }

    public String printerSetting(String machine_code, int usb_print_mode, int usb_input_mode, int camera_decode_tx_mode) throws Exception {
        String timestamp = Utils.getTimestamp();
        String signMD5 = clientId + timestamp + clientSecret;
        String sign = Utils.getMD5Str(signMD5);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("access_token", token);
        paramMap.put("machine_code", machine_code);
        paramMap.put("client_id", clientId);
        paramMap.put("timestamp", timestamp);
        paramMap.put("id", UUID.randomUUID().toString());
        paramMap.put("sign", sign);
        paramMap.put("usb_print_mode", String.valueOf(usb_print_mode));
        paramMap.put("usb_input_mode", String.valueOf(usb_input_mode));
        paramMap.put("camera_decode_tx_mode", String.valueOf(camera_decode_tx_mode));
        return HttpRequest.sendPost(UtilUrl.getPrinterSetting(), paramMap);
    }

    private static class HttpRequest {

        public static String sendGet(String url, Map<String, String> paramMap) throws Exception {
            String param = forMap(paramMap);
            StringBuilder result = new StringBuilder();
            String urlNameString = url + "?" + param;
            URLConnection connection = getUrlConnection(urlNameString);
            connection.connect();

            String line;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }

            return panicIfError(result.toString());
        }

        public static String sendPost(String url, Map<String, String> paramMap) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");

            MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
            paramMap.forEach(multiValueMap::add);
            ResponseEntity<String> response = new RestTemplate()
                    .postForEntity(url, new HttpEntity<>(multiValueMap, headers), String.class);

            String body = response.getBody();
            if (body == null) {
                throw new RuntimeException("电子面单申请失败，请重试");
            }

            return panicIfError(body);
        }

        private static URLConnection getUrlConnection(String url) {
            try {
                URL realUrl = new URL(url);
                URLConnection conn = realUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "Content-Type:application/x-www-form-urlencoded");
                conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                return conn;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static String forMap(Map<String, String> paramMap) {
            if (ObjectUtil.isEmpty(paramMap)) {
                return "";
            }

            StringBuilder reqStr = new StringBuilder();
            for (Map.Entry<String, String> stringStringEntry : paramMap.entrySet()) {
                reqStr.insert(0, stringStringEntry.getKey() + "=" + stringStringEntry.getValue() + "&");
            }

            return reqStr.substring(0, reqStr.length() - 1);
        }

        private static String panicIfError(String output) {
            CommonResponse response = JSONUtil.toBean(output, CommonResponse.class);
            if (ObjectUtil.hasEmpty(output, response)) {
                throw new RuntimeException("illegal response :" + output);
            }

            if (response.getError() != 0 && !"success".equals(response.getErrorDescription())) {
                throw new RuntimeException(response.getErrorDescription());
            }

            log.debug("response:{}", output);

            return response.getBody();
        }

    }

    @Data
    private static class CommonResponse {
        private int error;
        @JsonProperty("error_description")
        private String errorDescription;
        private int timestamp;
        private String body;
    }
}
