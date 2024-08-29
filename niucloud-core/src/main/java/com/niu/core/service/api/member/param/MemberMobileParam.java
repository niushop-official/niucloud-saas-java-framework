package com.niu.core.service.api.member.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class MemberMobileParam implements ApiBaseParam {

    private String mobile;

    private String mobileCode;

    private String mobileKey = "";
}
