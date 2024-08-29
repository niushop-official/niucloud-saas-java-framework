package com.niu.core.listener.example;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.component.context.event.Event;
import com.niu.core.component.context.listener.AbstractListener;
import org.springframework.stereotype.Component;

/**
 * 异步事件，无返回值
 */
@EventListen("core")
@Component
public class CoreEventListener extends AbstractListener {

    @Override
    public void handleEvent(Event event) {
        System.out.println("我是异步事件的监听.");
    }

}
