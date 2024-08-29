package com.niu.core.service.admin.diy.param;

import cn.hutool.json.JSONArray;
import lombok.Data;
import cn.hutool.json.JSONArray;
import java.io.Serializable;

import java.io.Serializable;

/**
 * 自定义页面参数
 */
@Data
public class SetDiyDataParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String key;

    private String type = "";

    private String addon = "";

    private Integer isStart;

    private JSONArray mainApp;
}