package com.niu.core.enums.pay;

import com.niu.core.common.utils.json.JsonModuleLoader;
import cn.hutool.json.JSONObject;

/**
 * 支付方式
 */
public class PayTypeEnum {

    public static JSONObject getType(){
        return JsonModuleLoader.build().mergeResultElement("pay/pay.json");
    }
}