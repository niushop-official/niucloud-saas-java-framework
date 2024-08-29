package com.niu.core.entity.diy;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * 底部导航数据
 */
@Data
public class BottomConfigValue implements Serializable {
    private static final long serialVersionUID = 1L;

    private String backgroundColor;

    private String textColor;

    private String textHoverColor;

    private String type;

    private JSONArray list;

}
