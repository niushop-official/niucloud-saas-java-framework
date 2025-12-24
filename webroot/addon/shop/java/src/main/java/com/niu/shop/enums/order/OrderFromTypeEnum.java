package com.niu.shop.enums.order;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.enums.common.ChannelEnum;

import java.util.Map;

public class OrderFromTypeEnum {

    public static JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("order/order_from.json");
    }

    public static Map<String, String> getOrderFrom() {
        Map<String, String> channel = ChannelEnum.getMap();
        JSONObject type = getType();
        for (String key : type.keySet()) {
            channel.put(key, type.getStr(key));
        }
        return channel;
    }
}
