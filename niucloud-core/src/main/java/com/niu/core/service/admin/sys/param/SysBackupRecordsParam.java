package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysBackupRecordsParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 备份版本号 */
    private String version;

    /** 备份标识 */
    private String backupKey;

    /** 备份内容 */
    private String content;

    /** 状态 */
    private String status;

    /** 失败原因 */
    private String failReason;

    /** 备注 */
    private String remark;

    /** 完成时间 */
    private Long completeTime;
}
