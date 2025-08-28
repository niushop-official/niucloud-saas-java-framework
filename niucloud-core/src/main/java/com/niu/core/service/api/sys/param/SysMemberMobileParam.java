package com.niu.core.service.api.sys.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class SysMemberMobileParam implements ApiBaseParam {

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 渠道
     */
    private String channel;

}
