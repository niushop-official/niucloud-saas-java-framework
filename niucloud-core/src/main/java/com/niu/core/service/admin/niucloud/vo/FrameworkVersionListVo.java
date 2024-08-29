package com.niu.core.service.admin.niucloud.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FrameworkVersionListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer appId;

    private String createTime;

    private Integer niucloudVersionId;

    private String releaseTime;

    private String updateTime;

    private String upgradeLog;

    private Integer versionId;

    private String versionNo;
}
