package com.niu.core.service.admin.weapp.vo;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * weapp固定配置信息
 */
@Data
public class WeappStaticInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serveUrl;
    private String requestUrl;
    private String socketUrl;
    private String uploadUrl;
    private String downloadUrl;
    private String uploadIp;

}
