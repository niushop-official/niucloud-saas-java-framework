package com.niu.core.service.admin.sys.vo;

import lombok.Data;

@Data
public class SysLoginConfigVo {
    private static final long serialVersionUID = 1L;

    private Integer isCaptcha = 0;
    private Integer isSiteCaptcha = 0;
    private String bg = "";
    private String siteBg = "";
}
