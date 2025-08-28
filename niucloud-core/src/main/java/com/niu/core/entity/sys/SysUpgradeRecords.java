package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SysUpgradeRecords {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 升级标识
     */
    private String upgradeKey;
    /**
     * 插件标识
     */
    private String appKey;
    /**
     * 升级名称
     */
    private String name;
    /**
     * 升级内容
     */
    private String content;
    /**
     * 前一版本
     */
    private String prevVersion;
    /**
     * 当前版本
     */
    private String currentVersion;
    /**
     * 状态
     */
    private String status;
    /**
     * 失败原因
     */
    private String failReason;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 完成时间
     */
    private Long completeTime;
}
