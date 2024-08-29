package com.niu.core.common.utils.http;

import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class MapUrlParamsUtils {

    /**
     * @param urlParam
     * @return
     */
    public static Map<String, Object> toMap(String urlParam) {
        Map<String, Object> paramsMap = new HashMap<String, Object>(0);
        if (StringUtils.isBlank(urlParam)) {
            return paramsMap;
        }
        String[] params = urlParam.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 1) {
                paramsMap.put(p[0], "");
            }
            if (p.length == 2) {
                String value = p[1];
                try {
                    value = URLDecoder.decode(value, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                paramsMap.put(p[0], value);
            }
        }
        return paramsMap;
    }

    /**
     * @param paramsMap
     * @return
     */
    public static String toUrlParam(Map<String, Object> paramsMap) {
        if (paramsMap == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String urlParam = sb.toString();
        if (urlParam.endsWith("&")) {
            urlParam = StringUtils.substringBeforeLast(urlParam, "&");
        }
        return urlParam;
    }

}
