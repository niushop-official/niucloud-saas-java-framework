package com.niu.core.component.context.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 事件发布者（同步）
 */
public class CallbackPublisher {

    // 事件发布参数，是否通过spring
    static final boolean useSpring = true;

    /**
     * 发布主题/事件
     *
     * @param businessEvent
     * @return
     */
    public static <T extends EventResult> Map<String, T> publish(Event businessEvent) {
        if (!useSpring) {
            return CallbackMediator.getInstance().handle(businessEvent);
        }
        return CallbackMediator.getInstance().handleBySpring(businessEvent);
    }

    /**
     * 发布主题/事件
     *
     * @param event
     * @return
     */
    public static <T extends EventResult> List<T> publishReturnList(Event event) {
        Map<String, T> callbackMap = null;
        if (!useSpring) {
            callbackMap = CallbackMediator.getInstance().handle(event);
        }
        callbackMap = CallbackMediator.getInstance().handleBySpring(event);
        if (callbackMap != null) {
            return new ArrayList<>(callbackMap.values());
        }
        return new ArrayList<>();
    }

    /**
     * 发布主题/事件
     *
     * @param event
     * @return
     */
    public static <T extends EventResult> Map<String, T> publishEvent(Event event) {
        return publish(event);
    }

    /**
     * 发布主题/事件
     *
     * @param event
     * @return
     */
    public static <T extends EventResult> List<T> publishEventReturnList(Event event) {
        Map<String, T> callbackMap = publish(event);
        if (callbackMap != null) {
            return new ArrayList<>(callbackMap.values());
        }
        return new ArrayList<>();
    }

}
