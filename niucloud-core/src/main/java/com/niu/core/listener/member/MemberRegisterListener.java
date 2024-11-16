package com.niu.core.listener.member;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.member.MemberRegisterEvent;
import com.niu.core.service.admin.member.param.MemberAccountLogParam;
import com.niu.core.service.core.member.ICoreMemberService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@EventListen("core")
@Component
public class MemberRegisterListener extends AbstractListener {

    public void handleEvent(MemberRegisterEvent event) {

        ICoreMemberService coreMemberService = (ICoreMemberService) SpringContext.getBean(ICoreMemberService.class);

        // 注册发放成长值
        Map<String, Object> sendGrowthParam = new HashMap<>();
        sendGrowthParam.put("from_type", "member_register");
        coreMemberService.sendGrowth(event.getSiteId(), event.getMember().getMemberId(), "member_register", sendGrowthParam);

        // 注册发放积分
        Map<String, Object> sendPointParam = new HashMap<>();
        sendPointParam.put("from_type", "member_register");
        coreMemberService.sendPoint(event.getSiteId(), event.getMember().getMemberId(), "member_register", sendPointParam);
    }
}
