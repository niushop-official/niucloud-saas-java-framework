package com.niu.core.service.api.sys.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

import java.util.Map;

@Data
public class SysVerifyGetCodeParam implements ApiBaseParam {

    private String type;

    private Map<String,Object> data;

    private Integer needBarcode = 0;

}
