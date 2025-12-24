package com.niu.core.service.admin.niucloud.vo;

import com.niu.core.service.admin.notice.vo.SmsTypeParamVo;
import lombok.Data;

import java.util.Map;

@Data
public class AuthInfoVo {
    private static final long serialVersionUID = 1L;

    @Data
    public static class AuthInfo {
        private Boolean addressType;
        private String authCode;
        private String companyName;
        private String contactName;
        private String mobile;
        private String productKey;
        private String siteAddress;
    }

    private AuthInfo data;

}
