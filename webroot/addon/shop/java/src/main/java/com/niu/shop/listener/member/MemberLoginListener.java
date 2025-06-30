package com.niu.shop.listener.member;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.member.MemberLoginEvent;
import com.niu.core.event.member.MemberRegisterEvent;
import com.niu.shop.service.core.marketing.ICoreShopNewcomerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@EventListen("shop")
@Component
public class MemberLoginListener extends AbstractListener {

    @Resource
    private ICoreShopNewcomerService coreShopNewcomerService;

    public void handleEvent(MemberLoginEvent event) {
        //登录后判断是否是新人专享活动新人，如果未参与过，给与参与资格
        coreShopNewcomerService.checkIfNewcomer(event.getSiteId(), event.getMember().getMemberId());
    }
}
