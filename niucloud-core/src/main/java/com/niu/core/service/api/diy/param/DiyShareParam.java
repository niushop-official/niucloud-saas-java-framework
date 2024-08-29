package com.niu.core.service.api.diy.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class DiyShareParam implements ApiBaseParam {

    private String route;

    private String params;

}
