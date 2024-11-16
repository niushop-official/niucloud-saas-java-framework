package com.niu.core.common.component.context.event;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.RequestUtils;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 事件（异步）发布者
 */
public class EventPublisher {

    /**
     * 发布事件
     *
     * @param event
     */
    public static void publishEvent(Event event) {
        ApplicationEventPublisher applicationEventPublisher = SpringContext.getApplicationEventPublisher();
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * @param event
     */
    public static void publish(Event event) {
        publish(event, null);
    }

    /**
     * @param event
     * @param siteId
     */
    public static void publish(Event event, Integer siteId) {
        if (siteId == null) {
            event.setSiteId(RequestUtils.siteId());
        }
        publishEvent(event);
    }

}
