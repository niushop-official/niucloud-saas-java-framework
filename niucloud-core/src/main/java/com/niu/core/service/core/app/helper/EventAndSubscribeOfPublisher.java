package com.niu.core.service.core.app.helper;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.event.CallbackPublisher;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.service.core.site.factory.CoreSiteServiceFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;

/**
 * 事件、订阅发布/事件、订阅发布回调......助手类
 */
public class EventAndSubscribeOfPublisher {

    /**
     * @param businessEvent
     */
    public static void publishAll(Event businessEvent) {
        businessEvent.setAuthority(false);
        ApplicationEventPublisher applicationEventPublisher = SpringContext.getApplicationEventPublisher();
        applicationEventPublisher.publishEvent(businessEvent);
    }

    /**
     * @param businessEvent
     */
    public static void publishBySiteId(Event businessEvent) {
        if (businessEvent == null) {
            return;
        }
        if (!businessEvent.isAuthority()) {
            ApplicationEventPublisher applicationEventPublisher = SpringContext.getApplicationEventPublisher();
            applicationEventPublisher.publishEvent(businessEvent);
        } else {
            // 进行授权控制
            Set<String> addonNameSet = CoreSiteServiceFactory.getCoreSiteGroupService().getAddonNamesBySiteId(businessEvent.getSiteId());
            businessEvent.addAppSign(new ArrayList<>(addonNameSet));
            ApplicationEventPublisher applicationEventPublisher = SpringContext.getApplicationEventPublisher();
            applicationEventPublisher.publishEvent(businessEvent);
        }
    }


    /**
     * @param businessEvent
     * @param <T>
     * @return
     */
    public static <T> List<T> publishAndCallback(Event businessEvent) {
        List<EventResult> businessEventResultList = publishAndCallbackListAll(businessEvent);
        List<T> tObjectList = new ArrayList<>();
        if (businessEventResultList != null && businessEventResultList.size() > 0) {
            businessEventResultList.stream().forEach(bean -> {
                tObjectList.add((T) bean);
            });
        }
        return tObjectList;
    }

    /**
     * @param businessEvent
     * @param <T>
     * @return
     */
    public static <T> List<T> publishAndCallbackBySite(Event businessEvent) {
        List<EventResult> businessEventResultList = publishAndCallbackListBySiteId(businessEvent);
        List<T> tObjectList = new ArrayList<>();
        if (businessEventResultList != null && businessEventResultList.size() > 0) {
            businessEventResultList.stream().forEach(bean -> {
                tObjectList.add((T) bean);
            });
        }
        return tObjectList;
    }


    /**
     * 发布事件并返回回调结果
     *
     * @param businessEvent
     * @return
     */
    static List<EventResult> publishAndCallbackListAll(Event businessEvent) {
        businessEvent.setAuthority(false);
        return CallbackPublisher.publishReturnList(businessEvent);
    }

    /**
     * 发布事件并返回回调结果
     *
     * @param businessEvent
     * @return
     */
    static List<EventResult> publishAndCallbackListBySiteId(Event businessEvent) {
        Map<String, EventResult> callbackMap = publishAndCallbackBySiteId(businessEvent);
        if (callbackMap != null) {
            return new ArrayList<>(callbackMap.values());
        }
        return new ArrayList<>();
    }

    /**
     * 发布事件并返回回调结果
     *
     * @param businessEvent
     */
    static Map<String, EventResult> publishAndCallbackAll(Event businessEvent) {
        businessEvent.setAuthority(false);
        return CallbackPublisher.publish(businessEvent);
    }

    /**
     * 发布事件并返回回调结果
     *
     * @param businessEvent
     */
    static Map<String, EventResult> publishAndCallbackBySiteId(Event businessEvent) {
        if (businessEvent == null) {
            return new HashMap<>();
        }
        if (!businessEvent.isAuthority()) {
            return CallbackPublisher.publish(businessEvent);
        } else {
            // 进行授权控制
            Set<String> addonNameSet = CoreSiteServiceFactory.getCoreSiteGroupService().getAddonNamesBySiteId(businessEvent.getSiteId());
            businessEvent.addAppSign(new ArrayList<>(addonNameSet));
            return CallbackPublisher.publish(businessEvent);
        }
    }

}
