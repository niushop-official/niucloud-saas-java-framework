package com.niu.core.common.loader.pay;

import cn.hutool.json.JSONObject;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.SystemLoader;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class PayLoader extends SystemLoader {

    /**
     * 获取所有消息驱动
     *
     * @return
     */
    public static JSONObject getType() {
        JSONObject jsonObject = JsonModuleLoader.build().mergeResultElement("pay/pay.json");
        return jsonObject;
    }

    /**
     * @param key
     * @return
     */
    public static BasePay getDriver(String key) {
        JSONObject jsonObject = PayLoader.getType();
        JSONObject keyJson = jsonObject.getJSONObject(key);
        BasePay base = BasePay.forName(keyJson.getStr("driver", ""));
        if (base == null) throw new CommonException("支付驱动不存在");
        return base;
    }

}
