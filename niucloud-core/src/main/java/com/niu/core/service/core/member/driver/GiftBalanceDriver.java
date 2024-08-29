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
public class GiftBalanceDriver {

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
                return config.getStr("money") + "元红包";
            case "member_level":
                JSONArray levelContent = new JSONArray();
                JSONObject levelContentItem = new JSONObject();
                levelContentItem.set("text", config.getStr("money") + "元");
                levelContentItem.set("background", "static/resource/images/member/gift/gift_balance_bg.png");
                levelContent.add(levelContentItem);
                return levelContent;
            case "member_sign":
                JSONObject signContent = new JSONObject();
                signContent.set("text", config.getStr("money") + "元");
                signContent.set("icon", "static/resource/images/member/sign/packet.png");
                return signContent;
            case "member_sign_continue":
                JSONObject signContinueContent = new JSONObject();
                signContinueContent.set("text", config.getStr("money") + "元");
                signContinueContent.set("icon", "static/resource/images/member/sign/packet01.png");
                return signContinueContent;
            default:
                return "";
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
                    AccountTypeEnum.BALANCE.getType(),
                    new Double(config.getStr("money")),
                    vars.getOrDefault("from_type", "").toString(),
                    vars.getOrDefault("momo", "").toString(),
                    vars.getOrDefault("related_id", "").toString());
        } catch (Exception e) {
            System.out.println("红包发放失败");
            e.printStackTrace();
        }
    }
}
