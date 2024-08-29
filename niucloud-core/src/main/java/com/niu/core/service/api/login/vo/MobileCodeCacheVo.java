package com.niu.core.service.api.login.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * 手机验证码视图
 */
@Data
public class MobileCodeCacheVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mobile;

    private String type;

    private String code;
}
