package com.niu.core.service.admin.wechat;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

/**
 * 微信配置
 */
public interface IWechatMenuService {

    JSONArray info();

    void edit(JSONArray data);
}
