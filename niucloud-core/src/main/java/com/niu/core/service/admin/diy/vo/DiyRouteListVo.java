package com.niu.core.service.admin.diy.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * DiyRouteVo
 */
@Data
public class DiyRouteListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;  // 页面名称
    private String name;  // 页面标识
    private String parent;
    private String page;  // 页面路径
    private Integer isShare;  // 是否支持分享
    private String action;
    private Integer sort;  // 排序
    private JSONObject addonInfo;
}
