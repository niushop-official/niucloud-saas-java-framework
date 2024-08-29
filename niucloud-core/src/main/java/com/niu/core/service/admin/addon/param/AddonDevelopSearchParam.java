package com.niu.core.service.admin.addon.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 插件日志参数
 */
@Data
public class AddonDevelopSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String search;

}
