package com.niu.core.service.admin.addon.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * AddonLogVo
 */
@Data
public class AddonLogListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private String action;  // 操作类型   install 安装 uninstall 卸载 update 更新
    private String key;  // 插件标识
    private String fromVersion;  // 升级前的版本号
    private String toVersion;  // 升级后的版本号
    private String createTime; // 创建时间

}
