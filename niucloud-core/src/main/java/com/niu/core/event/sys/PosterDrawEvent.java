package com.niu.core.event.sys;

import cn.hutool.json.JSONObject;
import com.freewayso.image.combiner.ImageCombiner;
import com.niu.core.common.component.context.event.Event;
import lombok.Data;

import java.util.Map;

@Data
public class PosterDrawEvent extends Event {
    /**
     * 海报绘制对象
     */
    private ImageCombiner combiner;

    /**
     * 绘制项目
     */
    private JSONObject poster;

    /**
     * 海报数据
     */
    private Map<String, Object> posterData;
}
