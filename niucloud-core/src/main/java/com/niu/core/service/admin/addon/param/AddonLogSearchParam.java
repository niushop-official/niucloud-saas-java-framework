package com.niu.core.service.admin.addon.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 插件日志参数
 */
@Data
public class AddonLogSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String action;

    private String key;

    private String fromVersion;

    private String toVersion;

}
