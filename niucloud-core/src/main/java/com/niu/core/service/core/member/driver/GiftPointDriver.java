package com.niu.core.service.core.member.driver;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.component.context.SpringContext;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class GiftPointDriver {

    public ICoreMemberAccountService coreMemberAccountService() {
        return SpringContext.bean(ICoreMemberAccountService.class);
    }

    /**
     * 获取礼包内容
     * @param siteId
     * @param config
     * @param scene
     * @return
     */
    public Object content(Integer siteId, JSONObject config, String scene) {
        switch (scene) {
            case "admin":
                return config.getStr("num") + "积分";
            case "member_level":
                JSONArray levelContent = new JSONArray();
                JSONObject levelContentItem = new JSONObject();
                levelContentItem.set("text", config.getStr("num") + "积分");
                levelContentItem.set("background", "static/resource/images/member/gift/gift_point_bg.png");
                levelContent.add(levelContentItem);
                return levelContent;
            case "member_sign":
                JSONObject signContent = new JSONObject();
                signContent.set("text", config.getStr("num") + "积分");
                signContent.set("icon", "static/resource/images/member/sign/point.png");
                return signContent;
            case "member_sign_continue":
                JSONObject signContinueContent = new JSONObject();
                signContinueContent.set("text", config.getStr("num") + "积分");
                signContinueContent.set("icon", "static/resource/images/member/sign/point01.png");
                return signContinueContent;
            default:
                return null;
        }
    }

    /**
     * 发放礼包
     */
    public void grant(Integer siteId, Integer memberId, JSONObject config, Map<String, Object> vars) {
        try {
            coreMemberAccountService().addLog(
                    siteId,
                    memberId,
                    AccountTypeEnum.POINT.getType(),
                    new Double(config.getStr("num")),
                    vars.getOrDefault("from_type", "").toString(),
                    vars.getOrDefault("memo", "").toString(),
                    vars.getOrDefault("related_id", "").toString());
        } catch (Exception e) {
            System.out.println("积分发放失败");
            e.printStackTrace();
        }
    }
}
