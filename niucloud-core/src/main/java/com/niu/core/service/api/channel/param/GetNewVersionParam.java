package com.niu.core.service.api.channel.param;

import lombok.Data;

@Data
public class GetNewVersionParam {

    private String platform;

    private Integer versionCode;
}
