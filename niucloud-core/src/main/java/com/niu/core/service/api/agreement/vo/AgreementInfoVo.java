package com.niu.core.service.api.agreement.vo;

import lombok.Data;

@Data
public class AgreementInfoVo {

    private Integer siteId;

    private String agreementKey;

    private String agreementKeyName;

    private String title = "";

    private String content = "";

    /** 添加时间 */
    private Long createTime;

    /** 修改时间 */
    private Long updateTime;

}
