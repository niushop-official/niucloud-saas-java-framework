package com.niu.core.common.component.upload.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ThumbModelResult {

    private boolean result;

    private Map<String, String> dataMap = new HashMap<>();

    public ThumbModelResult() {
        this.result = true;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public ThumbModelResult addDataMap(String key, String value) {
        dataMap.put(key, value);
        return this;
    }

}
