package com.niu.core.common.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "system")
@Data
public class SystemConfig {
    //后端token名称
    private String adminTokenName;
    //前台token名称
    private String apiTokenName;
    //后台site_id名称
    private String adminSiteIdName;
    //前台site_id名称
    private String apiSiteIdName;
    //渠道名称
    private String channelName;

}
