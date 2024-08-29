package com.niu.core.service.api.member.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class MemberSignMonthRecordParam implements ApiBaseParam {

    private String year;

    private String month;

}
