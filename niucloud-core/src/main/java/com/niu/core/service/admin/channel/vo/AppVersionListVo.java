package com.niu.core.service.admin.channel.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.channel.AppDict;
import lombok.Data;

/**
 * 版本列表VO
 */
@Data
public class AppVersionListVo {

    /**
     * 版本ID
     */
    private Integer id;

    /**
     * 平台类型
     */
    private String platform;

    /**
     * 平台名称
     */
    private String platformName;

    /**
     * 版本号
     */
    private String versionName;

    /**
     * 版本代码
     */
    private String versionCode;

    /**
     * 应用ID
     */
    private String applicationId;

    /**
     * 版本状态
     */
    private String status;

    /**
     * 状态名称
     */
    private String statusName;

    private String failReason;

    /**
     * 强制更新
     */
    private Integer forceUpdate;

    private String upgradeType;

    private String packagePath;

    private String taskKey;

    /**
     * 创建时间
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;
    /**
     * 发布时间
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long releaseTime;

    public String getPlatformName() {
        return AppDict.getPlatformName(platform);
    }

    public String getStatusName() {
        return AppDict.getStatusName(status);
    }
}