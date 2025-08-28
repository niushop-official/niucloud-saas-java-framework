package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SysBackupRecords {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 备份版本号
     */
    private String version;
    /**
     * 备份标识
     */
    private String backupKey;
    /**
     * 备份内容
     */
    private String content;
    /**
     * 状态
     */
    private String status;
    /**
     * 失败原因
     */
    private String failReason;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 完成时间
     */
    private Long completeTime;
}
