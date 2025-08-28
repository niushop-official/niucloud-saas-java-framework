package com.niu.core.service.admin.diy.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * 自定义主题色设置
*/
@Data
public class DiyThemeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String addon;

    private String title;

    private JSONObject theme;

    private JSONArray newTheme;

    private JSONObject defaultTheme;

}
