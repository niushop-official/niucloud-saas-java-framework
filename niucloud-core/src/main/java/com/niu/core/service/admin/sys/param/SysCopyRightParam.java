package com.niu.core.service.admin.sys.param;

import lombok.Data;

@Data
public class SysCopyRightParam {
    private static final long serialVersionUID = 1L;

    private String icp;
    private String govRecord;
    private String govUrl;
    private String marketSupervisionUrl;
    private String logo;
    private String companyName;
    private String copyrightLink;
    private String copyrightDesc;
}
