package com.niu.core.service.core.member.driver;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class BenefitsDriver {

    public Object content(Integer siteId, JSONObject config, String scene) {
        switch (scene) {
            case "admin":
                return "下单享受"+ config.getStr("discount") +"折折扣";
            case "member_level":
                JSONObject memberLevelBenefits = new JSONObject();
                memberLevelBenefits.set("title", "可享"+ config.getStr("discount") +"折");
                memberLevelBenefits.set("desc", "购物折扣");
                memberLevelBenefits.set("icon", "/static/resource/images/member/benefits/benefits_discount.png");
                return memberLevelBenefits;
            default:
                return null;
        }
    }
}
