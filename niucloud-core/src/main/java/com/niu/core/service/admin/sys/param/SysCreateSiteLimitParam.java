package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysCreateSiteLimitParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer groupId;
    private Integer num;
    private Integer month;
}
