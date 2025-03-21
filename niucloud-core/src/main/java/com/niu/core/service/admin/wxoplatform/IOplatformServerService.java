package com.niu.core.service.admin.wxoplatform;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.admin.wxoplatform.param.OplatformServerParam;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;

/**
 * 微信配置服务层
 */
public interface IOplatformServerService {

    /**
     * 第三方平台授权事件接收
     * @param param
     */
    void server(OplatformServerParam param);

    /**
     * 第三方平台消息接收
     * @param appid
     * @param param
     */
    String message(String appid, OplatformServerParam param);
}
