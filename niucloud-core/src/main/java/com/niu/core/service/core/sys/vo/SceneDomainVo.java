package com.niu.core.service.core.sys.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SceneDomainVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String wapDomain;
    /** 手机端域名 */
    private String wapUrl;
    /** web端域名 */
    private String webUrl;
}
