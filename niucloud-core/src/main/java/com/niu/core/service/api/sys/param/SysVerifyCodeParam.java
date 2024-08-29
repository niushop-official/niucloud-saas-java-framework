package com.niu.core.service.api.sys.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class SysVerifyCodeParam implements ApiBaseParam {

    private String code;

}
