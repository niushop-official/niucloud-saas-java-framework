package com.niu.core.component.context.listener;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.component.context.event.Event;
import org.springframework.context.event.EventListener;

import java.lang.reflect.Method;

public abstract class AbstractListener {

    /**
     * 监听事件
     *
     * @param event
     */
    @EventListener
    public void handle(Event event) {
        String name = "";
        try {
            EventListen eventListen = this.getClass().getAnnotation(EventListen.class);
            name = eventListen.value();
        } catch (Exception e) {
            // ignore
        }
        if (StrUtil.isNotEmpty(name)) {
            if (event.isAuthority()) {
                // 做鉴权处理
                if (event.contain(name)) {
                    try {
                        Method method = this.getClass().getMethod("handleEvent", event.getClass());
                        method.invoke(this, (Object) event);
                    } catch (Exception e) {
                        // this.handleEvent(businessEvent);
                    }
                }
            } else {
                // 不做鉴权处理，全部接收
                try {
                    Method method = this.getClass().getMethod("handleEvent", event.getClass());
                    method.invoke(this, (Object) event);
                } catch (Exception e) {
                    // this.handleEvent(businessEvent);
                }
            }
        }
    }

    public void handleEvent(Event businessEvent) {

    }

}
