package com.niu.core.service.api.member.param;

import com.niu.core.entity.member.Member;
import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class MemberModifyParam implements ApiBaseParam {

    private String field;

    private Object value;

}
