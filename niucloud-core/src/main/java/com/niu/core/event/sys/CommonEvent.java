package com.niu.core.event.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 通用事件类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonEvent extends Event {

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 传输数据
     */
    private JSONObject data;

}
