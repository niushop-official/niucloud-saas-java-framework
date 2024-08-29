package com.niu.core.service.admin.wxoplatform;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;

/**
 * 微信配置服务层
 */
public interface IOplatformConfigService {

    /**
     * 查询微信需要的静态信息
     *
     * @return
     */
    CoreOplatformStaticConfigVo getOplatformStaticInfo();

    /**
     * 获取微信开放平台配置
     *
     * @return
     */
    JSONObject getWxOplatformConfig();

    /**
     * 更新微信开放平台配置
     *
     * @param oplatformConfigParam
     */
    void setWxOplatformConfig(OplatformConfigParam oplatformConfigParam);
}
