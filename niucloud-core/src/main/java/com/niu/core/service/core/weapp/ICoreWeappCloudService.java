package com.niu.core.service.core.weapp;

import cn.hutool.json.JSONObject;
import com.niu.core.service.core.weapp.param.WeappConfigParam;
import com.niu.core.service.core.weapp.param.WeappUploadParam;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;

/**
 * 微信配置core服务层
 */
public interface ICoreWeappCloudService {

    String uploadWeapp(WeappUploadParam param);

    JSONObject getWeappCompileLog(String key);

    String getWeappPreviewImage();
}
