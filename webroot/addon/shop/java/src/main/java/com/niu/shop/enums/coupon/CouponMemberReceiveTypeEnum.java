package com.niu.shop.enums.coupon;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class CouponMemberReceiveTypeEnum {

    public static JSONArray getReceiveType() {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet("shop_coupon/coupon_receive_type.json");
    }

    public static String getReceiveTypeName(String type) {
        JSONArray types = getReceiveType();
        for (int i = 0; i < types.size(); i++) {
            JSONObject item = types.getJSONObject(i);
            if (item.getStr("name", "").equals(type)) return item.getStr("title", "");
        }
        return "";
    }
}
