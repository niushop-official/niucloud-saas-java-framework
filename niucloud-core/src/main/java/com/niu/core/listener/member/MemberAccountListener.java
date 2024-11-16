package com.niu.core.listener.member;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.event.member.MemberAccountEvent;
import com.niu.core.service.core.member.ICoreMemberLevelService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@EventListen("core")
@Component
public class MemberAccountListener extends AbstractListener {

    public void handleEvent(MemberAccountEvent event) {
        // 会员升级检测
        if (event.getAccountType().equals(AccountTypeEnum.GROWTH.getType())) {
            ICoreMemberLevelService coreMemberLevelService = SpringContext.bean(ICoreMemberLevelService.class);
            coreMemberLevelService.checkLevelUpgrade(event.getSiteId(), event.getMemberId());
        }
    }
}
