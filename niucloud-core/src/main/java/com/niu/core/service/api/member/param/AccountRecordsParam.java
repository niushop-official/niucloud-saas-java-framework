package com.niu.core.service.api.member.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class AccountRecordsParam implements ApiBaseParam {

    private String fromType;

    private String accountType;

    private String[] createTime;

}
