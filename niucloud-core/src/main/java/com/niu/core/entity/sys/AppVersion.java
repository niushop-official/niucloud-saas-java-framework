package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * app版本管理
 */
@Data
public class AppVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 站点ID
     */
    @TableField("site_id")
    private Integer siteId;

    /**
     * 版本号
     */
    @TableField("version_code")
    private String versionCode;

    /**
     * 版本名称
     */
    @TableField("version_name")
    private String versionName;

    /**
     * 版本描述
     */
    @TableField("version_desc")
    private String versionDesc;

    /**
     * 平台类型 android/ios
     */
    @TableField("platform")
    private String platform;

    /**
     * 是否强制更新
     */
    @TableField("is_forced_upgrade")
    private Integer isForcedUpgrade;

    /**
     * 安装包路径
     */
    @TableField("package_path")
    private String packagePath;

    /**
     * 任务key
     */
    @TableField("task_key")
    private String taskKey;

    /**
     * 发布时间
     */
    @TableField("release_time")
    private Long releaseTime;

    /**
     * 状态 upload_success/create_fail/published/creating
     */
    @TableField("status")
    private String status;

    /**
     * 失败原因
     */
    @TableField("fail_reason")
    private String failReason;

    /**
     * 更新类型
     */
    @TableField("upgrade_type")
    private String upgradeType;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Long updateTime;
}