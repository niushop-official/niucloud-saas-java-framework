package com.niu.core.service.core.wxoplatform;

import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;

/**
 * 微信配置服务层
 */
public interface ICoreOplatformConfigService {

    /**
     * 查询微信需要的静态信息
     *
     * @return
     */
    CoreOplatformStaticConfigVo getOplatformStaticInfo();

    /**
     * 更新微信公众号配置信息
     *
     * @param oplatformConfigParam
     */
    void setOplatformConfig(OplatformConfigParam oplatformConfigParam);

}
