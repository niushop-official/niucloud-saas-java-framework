package com.niu.core.service.api.member.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class MemberCashOutListParam implements ApiBaseParam {

    private String status;

    private String createTime;

}
