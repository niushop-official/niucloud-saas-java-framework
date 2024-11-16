package com.niu.core.common.component.context.event;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.CallbackListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 媒介类（事件服务者）
 */
@Slf4j
public final class CallbackMediator {

    private static final Map<Class, Event> themeObjectMap = new HashMap<>();
    private static final Map<Class, List<CallbackListener>> callbackListenerMap = new HashMap<>();

    private static final CallbackMediator businessMediator = new CallbackMediator();

    private boolean init = false;

    private CallbackMediator() {

    }

    /**
     * 初始化
     *
     * @param callbackClassList
     */
    public synchronized void init(List<Class> callbackClassList) {
        if (!init) {
            init = true;
            if (callbackClassList != null && callbackClassList.size() > 0) {
                callbackClassList.stream().forEach(callbackClass -> {
                    initCallback(callbackClass);
                });
            }
        }
//        System.out.println("themeObjectMap => " + themeObjectMap);
        System.out.println("themeSubscriberMap => " + callbackListenerMap);
    }

    /**
     * @param callbackClass
     */
    private void initCallback(Class callbackClass) {
        try {
            Object subscriberObject = callbackClass.newInstance();
            if (subscriberObject instanceof CallbackListener) {
                Method method = getCallbackMethod(callbackClass);
                Class businessThemeClass = getCallbackType(method);
                if (businessThemeClass == null) {
                    return;
                }
                Object themeObject = themeObjectMap.get(businessThemeClass);
                if (themeObject == null) {
                    themeObject = businessThemeClass.newInstance();
                    if (themeObject instanceof Event) {
                        themeObjectMap.put(businessThemeClass, (Event) themeObject);
                    }
                }
                List<CallbackListener> callbackListenerList = callbackListenerMap.get(businessThemeClass);
                if (callbackListenerList == null) {
                    callbackListenerList = new ArrayList<>();
                }
                callbackListenerList.add((CallbackListener) subscriberObject);
                callbackListenerMap.put(businessThemeClass, callbackListenerList);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * @param method
     * @return
     */
    public Class getCallbackType(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes != null && parameterTypes.length > 0) {
            return parameterTypes[0];
        }
        return null;
    }

    /**
     * @param clazz
     * @return
     */
    public Method getCallbackMethod(Class clazz) {
        Method[] methods = clazz.getMethods();
        Method targetMethod = null;
        for (Method method : methods) {
            if (method.getName().equals("handleCallback")) {
                targetMethod = method;
                break;
            }
        }
        return targetMethod;
    }

    /**
     * @param businessEvent
     * @return
     */
    public <T extends EventResult> Map<String, T> handle(Event businessEvent) {
        Map<String, T> callbackResponseMap = new HashMap<>();
        List<CallbackListener> businessCallbackListenerList = callbackListenerMap.get(businessEvent.getClass());
        if (businessCallbackListenerList != null && businessCallbackListenerList.size() > 0) {
            for (CallbackListener businessCallbackListener : businessCallbackListenerList) {
                String modelName = businessCallbackListener.getModelName();
                if (businessEvent.isAuthority()) {
                    if (businessEvent.contain(modelName)) {
                        EventResult callbackResponseResult = null;
                        try {
                            callbackResponseResult = (EventResult) businessCallbackListener.handle(modelName, businessEvent);
                            callbackResponseResult.setName(modelName);
                            callbackResponseResult.setBusinessEvent(businessEvent);
                        } catch (Exception e) {
                            // ignore
                            throw e;
                        }
                        if (callbackResponseResult == null) {
                            callbackResponseMap.put(modelName, (T) callbackResponseResult);
                        } else {
                            callbackResponseMap.put(modelName, null);
                        }
                    }
                } else {
                    EventResult callbackResponseResult = null;
                    try {
                        callbackResponseResult = (EventResult) businessCallbackListener.handle(modelName, businessEvent);
                        callbackResponseResult.setName(modelName);
                        callbackResponseResult.setBusinessEvent(businessEvent);
                    } catch (Exception e) {
                        // ignore
                        throw e;
                    }
                    if (callbackResponseResult == null) {
                        callbackResponseMap.put(modelName, (T) callbackResponseResult);
                    } else {
                        callbackResponseMap.put(modelName, null);
                    }
                }
            }
        }
        return callbackResponseMap;
    }

    /**
     * @param businessEvent
     * @return
     */
    public <T extends EventResult> Map<String, T> handleBySpring(Event businessEvent) {
        Map<String, T> callbackResponseMap = new HashMap<>();
        List<CallbackListener> businessCallbackListenerList = callbackListenerMap.get(businessEvent.getClass());
        if (businessCallbackListenerList != null && businessCallbackListenerList.size() > 0) {
            for (CallbackListener businessCallbackListener : businessCallbackListenerList) {
                CallbackListener springBusinessCallbackListener = (CallbackListener) SpringContext.getBean(businessCallbackListener.getClass());
                String modelName = springBusinessCallbackListener.getModelName();
                if (businessEvent.isAuthority()) {
                    if (businessEvent.contain(modelName)) {
                        EventResult callbackResponseResult = null;
                        try {
                            callbackResponseResult = (EventResult) springBusinessCallbackListener.handle(modelName, businessEvent);
                            callbackResponseResult.setName(modelName);
                            callbackResponseResult.setBusinessEvent(businessEvent);
                        } catch (Exception e) {
                            // ignore
                            throw e;
                        }
                        callbackResponseMap.put(modelName, (T) callbackResponseResult);
                    }
                } else {
                    EventResult callbackResponseResult = null;
                    try {
                        callbackResponseResult = (EventResult) springBusinessCallbackListener.handle(modelName, businessEvent);
                        if (callbackResponseResult != null) {
                            callbackResponseMap.put(businessCallbackListener.getClass().getName(), (T) callbackResponseResult);
                        }
//                        if (callbackResponseResult == null) {
//                            callbackResponseResult = new DefaultEventResult();
//                        }
//                        callbackResponseResult.setName(modelName);
//                        callbackResponseResult.setBusinessEvent(businessEvent);
                    } catch (Exception e) {
                        // ignore
                        throw e;
                    }
                }
            }
        }
        return callbackResponseMap;
    }


    /**
     * @return
     */
    public static CallbackMediator getInstance() {
        return businessMediator;
    }

    /**
     * @param businessEvent
     * @return
     */
    public static List<CallbackListener> getSubscribers(Event businessEvent) {
        return callbackListenerMap.get(businessEvent.getClass());
    }

}
