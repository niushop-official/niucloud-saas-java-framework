package com.niu.shop.service.core.member.driver;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class PointRuleBuyGoodsDriver {

    /**
     * 获取发放的积分
     * @param siteId
     * @param config
     * @param key
     * @return
     */
    public Integer calculate(Integer siteId, String key, JSONObject config, Map<String, Object> data) {
        BigDecimal orderMoney = (BigDecimal) data.get("order_money");
        BigDecimal money = new BigDecimal(config.getStr("money"));
        return (int) Math.ceil(orderMoney.divide(money, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(config.getInt("point", 0))).doubleValue());
    }

    /**
     * 获取积分规则内容
     * @param siteId
     * @param config
     * @param scene
     * @return
     */
    public Object content(Integer siteId, JSONObject config, String scene) {
        switch (scene) {
            case "admin":
                return "每消费" + config.getStr("money") + "元可获得" + config.getStr("point") + "积分";
            default:
                return "";
        }
    }
}
