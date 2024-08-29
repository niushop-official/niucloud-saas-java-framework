package com.niu.core.service.api.member.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class MemberLogParam implements ApiBaseParam {

    private String route;

    private String params;

    private String preRoute;

}
