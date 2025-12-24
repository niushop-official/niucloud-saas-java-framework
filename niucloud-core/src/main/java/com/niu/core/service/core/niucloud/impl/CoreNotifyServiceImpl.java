package com.niu.core.service.core.niucloud.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.service.core.niucloud.ICoreNotifyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CoreNotifyServiceImpl implements ICoreNotifyService {

    @Override
    public Result notify(HttpServletRequest request, HttpServletResponse response) {
        String signature = ObjectUtil.defaultIfNull(request.getParameter("signature"), "");
        String timestamp = ObjectUtil.defaultIfNull(request.getParameter("timestamp"), "");
        String nonce = ObjectUtil.defaultIfNull(request.getParameter("nonce"), "");

        // 校验签名
        NiucloudUtils.Niucloud.checkSignature(signature, timestamp, nonce);

        JSONObject data = WebUtil.parseNestedParams(request);
        String msgType = data.getStr("MsgType", "");

        switch (msgType) {
            case "auth":
                Object token = data.getByPath("Message.AccessToken.token");
                if (ObjectUtil.isNotNull(token)) {
                    NiucloudUtils.Niucloud.setAccessToken(token.toString());
                }
                break;
        }

        return Result.success();
    }

    public static class WebUtil {

        /**
         * 将 HttpServletRequest 的参数（支持 a[b][c] 格式）解析为嵌套 JSONObject
         * 注意：每个参数只取第一个值（String[] -> String）
         */
        public static JSONObject parseNestedParams(HttpServletRequest request) {
            JSONObject root = new JSONObject();
            Map<String, String[]> paramMap = request.getParameterMap();

            for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                if (values == null || values.length == 0) continue;

                String value = values[0]; // 取第一个值
                setNestedValue(root, key, value);
            }
            return root;
        }

        /**
         * 根据 "a[b][c]" 格式的 key，向 JSONObject 中设置嵌套值
         */
        private static void setNestedValue(JSONObject obj, String key, Object value) {
            if (!key.contains("[")) {
                obj.set(key, value);
                return;
            }

            String cleanKey = key.replace(']', '[');
            String[] parts = cleanKey.split("\\[");

            JSONObject current = obj;
            for (int i = 0; i < parts.length; i++) {
                if (StrUtil.isEmpty(parts[i])) continue; // 跳过空段（如结尾的 ] 产生的空串）

                if (i == parts.length - 1) {
                    // 最后一段，赋值
                    current.set(parts[i], value);
                } else {
                    // 中间段，确保是 JSONObject
                    JSONObject next = current.getJSONObject(parts[i]);
                    if (next == null) {
                        next = new JSONObject();
                        current.set(parts[i], next);
                    }
                    current = next;
                }
            }
        }
    }
}
