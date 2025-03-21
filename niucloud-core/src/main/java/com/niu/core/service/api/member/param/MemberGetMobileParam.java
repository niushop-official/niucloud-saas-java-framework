package com.niu.core.service.api.member.param;

import com.niu.core.entity.member.MemberAddress;
import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class MemberGetMobileParam extends MemberAddress implements ApiBaseParam {


    private String mobileCode;
}
