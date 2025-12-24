package com.niu.core.service.api.sys.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class SysVerifyRecordsParam implements ApiBaseParam {

    private String relateTag;

    private String type;

    private String code;

    private String keyword;

    private String[] createTime;

    private Integer orderId;

}
