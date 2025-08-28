package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysUpgradeRecordsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 升级标识 */
    private String upgradeKey;

    /** 插件标识 */
    private String appKey;

    /** 升级名称 */
    private String name;

    /** 升级内容 */
    private String content;

    /** 前一版本 */
    private String prevVersion;

    /** 当前版本 */
    private String currentVersion;

    /** 状态 */
    private String status;

    /** 失败原因 */
    private String failReason;

    /** 完成时间 */
    private Long completeTime;
}
