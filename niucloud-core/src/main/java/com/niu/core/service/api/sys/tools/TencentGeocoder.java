package com.niu.core.service.api.sys.tools;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TencentGeocoder {

    /**
     * @param apiKey
     * @param location
     * @param getPoi
     * @return
     */
    public static String getAddress(String apiKey, String location, Integer getPoi) {
        try {
            if (getPoi != 1) {
                getPoi = 0;
            }
            // 请求URL
            StringBuilder requestUrlString = new StringBuilder();
            requestUrlString.append("https://apis.map.qq.com");
            requestUrlString.append("/ws/geocoder/v1?key=").append(apiKey);
            requestUrlString.append("&location=").append(location);
            requestUrlString.append("&get_poi=").append(getPoi);
            String urlStr = requestUrlString.toString();
            // System.out.println(urlStr);
            // 请求
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(urlStr);
            HttpResponse httpResponse = client.execute(request);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    /**
     * @param apiKey
     * @param location
     * @param getPoi
     * @param secretKey
     * @return
     */
    public static String getAddressBySign(String apiKey, String location, Integer getPoi, String secretKey) {
        try {
            if (getPoi != 1) {
                getPoi = 0;
            }
            // 签名URL
            StringBuilder signString = new StringBuilder();
            signString.append("/ws/geocoder/v1");
            signString.append("?get_poi=").append(getPoi);
            signString.append("&key=").append(apiKey);
            signString.append("&location=").append(location);
            signString.append(secretKey);
            String signValue = MD5.create().digestHex(signString.toString(), "UTF-8");
            // 请求URL
            StringBuilder requestUrlString = new StringBuilder();
            requestUrlString.append("https://apis.map.qq.com");
            requestUrlString.append("/ws/geocoder/v1?key=").append(apiKey);
            requestUrlString.append("&location=").append(location);
            requestUrlString.append("&get_poi=").append(getPoi);
            requestUrlString.append("&sig=").append(signValue);
            String urlStr = requestUrlString.toString();
            // System.out.println(urlStr);
            // 请求
            HttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(urlStr);
            HttpResponse httpResponse = client.execute(request);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    /**
     * @param jsonStr
     * @return
     */
    public static JSONObject formatData(String jsonStr) {
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
        if ("Success".equals(jsonObject.get("message"))) {
            JSONObject jsonObjectResult = new JSONObject();
            try {
                JSONObject resultObject = jsonObject.getJSONObject("result");
                String fullAddress = resultObject.getStr("address");
                JSONObject formattedAddresses = resultObject.getJSONObject("formatted_addresses");
                JSONObject addressComponent = resultObject.getJSONObject("address_component");
                String province = addressComponent.getStr("province");
                String city = addressComponent.getStr("city");
                String district = addressComponent.getStr("district");
                jsonObjectResult.putOpt("province", province);
                jsonObjectResult.putOpt("city", city);
                jsonObjectResult.putOpt("district", district);
                jsonObjectResult.putOpt("full_address", fullAddress);
                jsonObjectResult.putOpt("formatted_addresses", formattedAddresses);
                return jsonObjectResult;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }

    public static void main(String[] args) {
        String apiKey = "SP2BZ-6PKRZ-MNVX7-ZUQUJ-C5V2H-FJFFA";
        String latitude = "39.984154"; // 例如：北京天安门的纬度
        String longitude = "116.407399"; // 例如：北京天安门的经度
        String secretKey = "ALeKVtn4IGFl8ohOWJVkeYkUhZjl3C5u";
        String addressString = getAddressBySign(apiKey, latitude + "," + longitude, 0, secretKey);
        System.out.println(formatData(addressString));
    }

}