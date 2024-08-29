package com.niu.core.common.utils.json;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import javax.crypto.spec.SecretKeySpec;

public class JsonSignTools {

    /**
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String hmacSHA256(String data, String key) throws Exception {
        String algorithm = "HmacSHA256";
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance(algorithm);
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);
        mac.init(secret_key);
        byte[] array = mac.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String hmacMD5(String data, String key) throws Exception {
        String algorithm = "HmacMD5";
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance(algorithm);
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), algorithm);
        mac.init(secret_key);
        byte[] array = mac.doFinal(data.getBytes("UTF-8"));
        return byteArrayToHexString(array).toUpperCase();
    }

    /**
     * byte数组转换为HexString
     *
     * @param b
     * @return
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }

    /**
     * @param srcJson
     * @param signName
     * @param signKey
     * @return
     * @throws Exception
     */
    public static String jsonSignByHmacMD5(String srcJson, String signName, String signKey) throws Exception {
        srcJson = JsonSorted.sorted(srcJson);
        // System.out.println(srcJson);
        JSONObject jsonObject = JSON.parseObject(srcJson);
        jsonObject.put(signName, hmacMD5(srcJson, signKey));
        return JsonSorted.sorted(jsonObject.toJSONString());
    }

    /**
     * @param signJson
     * @param signName
     * @param signKey
     * @return
     * @throws Exception
     */
    public static boolean verifyJsonSignByHmacMD5(String signJson, String signName, String signKey) throws Exception {
        JSONObject jsonObject = JSON.parseObject(signJson);
        String signValue = jsonObject.getString(signName);
        if (signValue == null) {
            return false;
        }
        jsonObject.remove(signName);
        String targetJson = JsonSorted.sorted(jsonObject.toJSONString());
        String signCode = hmacMD5(targetJson, signKey);
        if (signValue.equalsIgnoreCase(signCode)) {
            return true;
        }
        return false;
    }


    /**
     * @param srcJson
     * @param signName
     * @param signKey
     * @return
     * @throws Exception
     */
    public static String jsonSignByHmacSHA256(String srcJson, String signName, String signKey) throws Exception {
        srcJson = JsonSorted.sorted(srcJson);
        JSONObject jsonObject = JSON.parseObject(srcJson);
        jsonObject.put(signName, hmacSHA256(srcJson, signKey));
        return JsonSorted.sorted(jsonObject.toJSONString());
    }

    /**
     * @param signJson
     * @param signName
     * @param signKey
     * @return
     * @throws Exception
     */
    public static boolean verifyJsonSignByHmacSHA256(String signJson, String signName, String signKey) throws Exception {
        JSONObject jsonObject = JSON.parseObject(signJson);
        String signValue = jsonObject.getString(signName);
        if (signValue == null) {
            return false;
        }
        jsonObject.remove(signName);
        String targetJson = JsonSorted.sorted(jsonObject.toJSONString());
        String signCode = hmacSHA256(targetJson, signKey);
        if (signValue.equalsIgnoreCase(signCode)) {
            return true;
        }
        return false;
    }
}
