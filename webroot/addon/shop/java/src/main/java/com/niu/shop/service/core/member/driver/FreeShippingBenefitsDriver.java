package com.niu.shop.service.core.member.driver;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class FreeShippingBenefitsDriver {

    public Object content(Integer siteId, JSONObject config, String scene) {
        switch (scene) {
            case "admin":
                return "下单享受包邮";
            case "member_level":
                JSONObject memberLevelBenefits = new JSONObject();
                memberLevelBenefits.set("title", "商品包邮");
                memberLevelBenefits.set("desc", "下单免运费");
                memberLevelBenefits.set("icon", "/static/resource/images/member/benefits/benefits_pinkage.png");
                return memberLevelBenefits;
            default:
                return null;
        }
    }
}
