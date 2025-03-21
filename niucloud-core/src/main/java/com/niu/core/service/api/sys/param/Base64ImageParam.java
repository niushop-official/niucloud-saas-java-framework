package com.niu.core.service.api.sys.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class Base64ImageParam implements ApiBaseParam {

    private String content;

}
