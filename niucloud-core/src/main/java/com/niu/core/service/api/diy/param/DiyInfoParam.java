package com.niu.core.service.api.diy.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class DiyInfoParam implements ApiBaseParam {

    private Integer id = 0;

    private String name = "";

}
