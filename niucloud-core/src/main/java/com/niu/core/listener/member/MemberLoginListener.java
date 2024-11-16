package com.niu.core.listener.member;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.member.MemberLoginEvent;

@EventListen("core")
public class MemberLoginListener extends AbstractListener {

    public void handleEvent(MemberLoginEvent event) {
        System.out.println("我是 MyCustomListener core, CoreBusinessEvent=============>" + event);
    }
}
