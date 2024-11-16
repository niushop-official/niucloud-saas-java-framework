package com.niu.core.common.component.context.event;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件返回抽象类
 */
@Data
@Accessors(chain = true)
public abstract class EventResult {

    // 应用及插件名称
    private String name;

    // 调用的事件本身
    private Event businessEvent;

    // 响应返回的数据
    private Map<String, Object> dataMap = new HashMap<>();

}
