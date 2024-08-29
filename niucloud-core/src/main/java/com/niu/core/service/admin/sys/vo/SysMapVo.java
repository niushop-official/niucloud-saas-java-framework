package com.niu.core.service.admin.sys.vo;

import lombok.Data;

@Data
public class SysMapVo {
    private static final long serialVersionUID = 1L;

    private String key = "IZQBZ-3UHEU-WTCVD-2464U-I5N4V-ZFFU3";
    private Integer isOpen = 1; // 是否开启定位
    private Integer validTime = 5; // 定位有效期/分钟，过期后将重新获取定位信息，0为不过期
}
