package com.niu.core.service.admin.niucloud.param;

import lombok.Data;

@Data
public class ConnectTestParam {
    private String url;
    private Integer isOpen = 0;
}
