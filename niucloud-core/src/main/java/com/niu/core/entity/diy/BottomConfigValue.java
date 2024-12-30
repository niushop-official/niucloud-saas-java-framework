package com.niu.core.entity.diy;

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 底部导航数据
 */
@Data
public class BottomConfigValue implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("backgroundColor")
    private String backgroundColor;

    @JsonProperty("textColor")
    private String textColor;

    @JsonProperty("textHoverColor")
    private String textHoverColor;

    private String type;

    private JSONArray list;

}
